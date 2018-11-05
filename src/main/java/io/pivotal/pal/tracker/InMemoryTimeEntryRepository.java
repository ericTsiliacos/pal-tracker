package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> inMemoryDataBase = new HashMap<>();
    private AtomicLong atomicLong = new AtomicLong();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        long id = atomicLong.incrementAndGet();
        TimeEntry createdTimeEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        inMemoryDataBase.put(createdTimeEntry.getId(), createdTimeEntry);
        return createdTimeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return inMemoryDataBase.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        inMemoryDataBase.replace(id, updatedTimeEntry);
        return inMemoryDataBase.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        Collection<TimeEntry> timeEntries = inMemoryDataBase.values();
        return new ArrayList<>(timeEntries);
    }

    @Override
    public void delete(long id) {
        inMemoryDataBase.remove(id);
    }
}
