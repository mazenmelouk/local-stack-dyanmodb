package dynamodbtest.config;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
public class TestService {


    private int numThreads = 10;
    @Autowired
    SectionItemRepository sectionItemRepository;


    @EventListener
    public void init(ContextRefreshedEvent event) throws InterruptedException {
        EnvironmentUtils.setEnv("AWS_CBOR_DISABLE", "1");
        System.out.println("Starting......");
        threads();
    }

    private void threads() throws InterruptedException {
        List<Task> tasks = IntStream.range(0, numThreads).mapToObj(value -> new Task(value)).collect(toList());
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        tasks.forEach(task -> executor.submit(task));
        System.out.println("Executing......");
        executor.shutdown();
        while (!executor.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
        System.out.println("Done......");
        LocalDate expected = LocalDate.now().plusDays(numThreads - 1);
        LocalDate persisted = StreamSupport.stream(sectionItemRepository.findAll().spliterator(), false).findFirst().get().getItems().values().stream().findFirst().get();
        if(!expected.equals(persisted)){
            System.err.println("Mismatch expected: "+expected+" persisted: "+persisted);
        }else{
            System.out.println("Passed");
        }

        sectionItemRepository.findAll().iterator().forEachRemaining(sectionItem1 -> sectionItem1.getItems().forEach((s, localDate) -> System.out.println("persisted" + localDate)));
    }

    class Task implements Runnable {
        int days;

        public Task(int days) {
            this.days = days;
        }

        @Override
        public void run() {
            randomSleep();
            print("running");
            save("section1", "item1", LocalDate.now().plusDays(days));
        }

        private void save(String section, String key, LocalDate timestamp) {

            boolean updateNeeded;
            SectionItem toPersist;
            do {
                SectionItem old = sectionItemRepository.findOne(section);
                if (old != null) {
                    old.getItems().forEach((s, localDate) -> print("persisted value is " + localDate));
                } else {
                    print("Nothing persisted");
                }
                updateNeeded = isUpdateNeeded(old, timestamp, key);
                toPersist = (old == null) ? new SectionItem(section, ImmutableMap.of(key, timestamp)) : old.withNewItems(key, timestamp);
                randomSleep();
            } while (updateNeeded && !saveSuccessfully(toPersist));

        }

        private void print(String s) {
            System.out.println("thread#" + days + "-" + s);
        }

        private void randomSleep() {
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean saveSuccessfully(SectionItem item) {
            try {
                item.getItems().forEach((s, localDate) -> print("persisting " + localDate));
                sectionItemRepository.save(item);
            } catch (ConditionalCheckFailedException cce) {
                System.err.println(cce);
                return false;
            }
            return true;
        }

        private boolean isUpdateNeeded(SectionItem old, LocalDate created, String key) {
            if (old != null) {
                Optional<LocalDate> lastUpdate = lastUpdateOf(key, old);
                return !lastUpdate.isPresent() || created.isAfter(lastUpdate.get());
            }
            return true;
        }

        private Optional<LocalDate> lastUpdateOf(String key, SectionItem old) {
            if (old.getItems().containsKey(key)) {
                return Optional.ofNullable(old.getItems().get(key));
            }
            return Optional.empty();
        }
    }

}
