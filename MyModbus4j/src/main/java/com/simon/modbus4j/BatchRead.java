package com.simon.modbus4j;

import com.simon.modbus4j.base.KeyedModbusLocator;
import com.simon.modbus4j.base.ReadFunctionGroup;
import com.simon.modbus4j.base.SlaveAndRange;
import com.simon.modbus4j.locator.BaseLocator;

import java.util.*;

public class BatchRead<K> {
    private final List<KeyedModbusLocator<K>> requestValues = new ArrayList<>();

    private boolean contiguousRequests = false;

    private boolean errorsInResults = false;

    private boolean excepitionsInResults = false;

    private boolean cancel;

    private List<ReadFunctionGroup<K>> functionGroups;

    public boolean isContiguousRequests() {
        return contiguousRequests;
    }

    public void setContiguousRequests(boolean contiguousRequests) {
        this.contiguousRequests = contiguousRequests;
        functionGroups = null;
    }

    public boolean isErrorsInResults() {
        return errorsInResults;
    }

    public void setErrorsInResults(boolean errorsInResults) {
        this.errorsInResults = errorsInResults;
    }

    public boolean isExceptionsInResults() {
        return excepitionsInResults;
    }

    public void setExceptionsInResults(boolean excepitionsInResults) {
        this.excepitionsInResults = excepitionsInResults;
    }

    public List<ReadFunctionGroup<K>> getReadFunctionGroups(ModbusMaster master) {
        if (functionGroups == null) {
            doPartition(master);
        }
        return functionGroups;
    }

    public void addLocator(K id, BaseLocator<?> locator) {
        addLocator(new KeyedModbusLocator<>(id, locator));
    }

    private void addLocator(KeyedModbusLocator<K> locator) {
        requestValues.add(locator);
        functionGroups = null;
    }

    public boolean isCancel(){
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    private void doPartition(ModbusMaster master) {
        Map<SlaveAndRange, List<KeyedModbusLocator<K>>> slaveRangeBatch = new HashMap<>();

        List<KeyedModbusLocator<K>> functionList;
        for (KeyedModbusLocator<K> locator : requestValues) {
            functionList = slaveRangeBatch.get(locator.getSlaveAndRange());
            if (functionList == null) {
                functionList = new ArrayList<>();
                slaveRangeBatch.put(locator.getSlaveAndRange(), functionList);
            }
            functionList.add(locator);
        }
        Collection<List<KeyedModbusLocator<K>>> functionLocatorLists = slaveRangeBatch.values();
        FunctionLocatorComparator comparator = new FunctionLocatorComparator();
        functionGroups = new ArrayList<>();
        for (List<KeyedModbusLocator<K>> functionLocatorList : functionLocatorLists) {
            Collections.sort(functionLocatorList, comparator);
            int maxReadCount = master.getMaxReadCount(functionLocatorList.get(0).getSlaveAndRange().getRange());
            createRequestGroups(functionGroups, functionLocatorList, maxReadCount);
        }
    }

    private void createRequestGroups(List<ReadFunctionGroup<K>> functionGroups, List<KeyedModbusLocator<K>> locators,
                                     int maxCount) {
        ReadFunctionGroup<K> functionGroup;
        KeyedModbusLocator<K> locator;
        int index;
        int endOffset;
        while (locators.size() > 0) {
            functionGroup = new ReadFucntionGroup<>(locators.remove(0));
            functionGroups.add(functionGroup);
            endOffset = functionGroup.getStartOffset() + maxCount - 1;

            index = 0;
            while (locators.size() > index) {
                locator = locators.get(index);
                boolean added = false;
                if (locator.getEndOffset() > endOffset) {
                    break;
                }
                index++;
            }
        }
    }

    class FunctionLocatorComparator implements Comparator<KeyedModbusLocator<K>> {
        @Override
        public int compare(KeyedModbusLocator<K> ml1, KeyedModbusLocator<K> ml2) {
            return ml1.getOffset() - ml2.getOffset();
        }
    }
}

