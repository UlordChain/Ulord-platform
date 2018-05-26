/**
 * Copyright(c) 2018
 * Ulord core developers
 */

import one.ulord.upaas.common.sync.SyncOpEnum;
import one.ulord.upaas.common.sync.SyncOpVersionRepo;
import one.ulord.upaas.common.sync.SyncOpItem;
import one.ulord.upaas.common.sync.SyncVersionException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author haibo
 * @since 5/23/18
 */
public class TestSyncHistory {
    @Test
    public void testSyncOpItem(){
        SyncOpItem item1 = new SyncOpItem(SyncOpEnum.ADD, "A");
        SyncOpItem item2 = new SyncOpItem(SyncOpEnum.ADD, "B");
        SyncOpItem item3 = new SyncOpItem(SyncOpEnum.ADD, "A");
        assertFalse(item1.equals(item2));
        assertTrue(item1.equals(item3));
        assertTrue(item1.hashCode() == item3.hashCode());
        assertFalse(item1.hashCode() == item2.hashCode());
    }

    @Test
    public void testSyncOpHistory(){
        SyncOpVersionRepo<String> stringHistory = new SyncOpVersionRepo<>();
        {
            List<SyncOpItem<String>> itemList = new ArrayList<>();
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "A"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "B"));
            itemList.add(new SyncOpItem(SyncOpEnum.DELETE, "A"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "C"));
            // version 0
            stringHistory.addRecordOp(itemList);
            List<String> result;
            result = stringHistory.getFullRecord();
            assertNotNull(result);
            assertTrue(result.size() == 2);
            assertTrue(result.get(0).equals("B"));
            assertTrue(result.get(1).equals("C"));
        }

        {
            // version 1
            List<SyncOpItem<String>> itemList = new ArrayList<>();
            itemList.add(new SyncOpItem(SyncOpEnum.DELETE, "C"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "D"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "E"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "F"));
            stringHistory.addRecordOp(itemList);
            assertTrue(stringHistory.isNewest(1));
            List<String> result = stringHistory.getFullRecord();
            assertNotNull(result);
            assertTrue(result.size() == 4);
            assertTrue(result.get(0).equals("B"));
            assertTrue(result.get(1).equals("D"));
            assertTrue(result.get(2).equals("E"));
            assertTrue(result.get(3).equals("F"));

            List<SyncOpItem<String>> opItems = stringHistory.getIncrementRecord(1);
            assertNotNull(opItems);
            assertTrue(opItems.size() == 4);
            assertEquals("C", opItems.get(0).getData());
            assertEquals("D", opItems.get(1).getData());
            assertEquals("E", opItems.get(2).getData());
            assertEquals("F", opItems.get(3).getData());

            assertEquals(SyncOpEnum.DELETE, opItems.get(0).getOp());
            assertEquals(SyncOpEnum.ADD, opItems.get(1).getOp());
            assertEquals(SyncOpEnum.ADD, opItems.get(2).getOp());
            assertEquals(SyncOpEnum.ADD, opItems.get(3).getOp());
        }

        // version 2
        {
            List<SyncOpItem<String>> itemList = new ArrayList<>();
            itemList.add(new SyncOpItem(SyncOpEnum.DELETE, "B"));
            itemList.add(new SyncOpItem(SyncOpEnum.DELETE, "D"));
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "G"));
            stringHistory.addRecordOp(itemList);
            assertTrue(stringHistory.isNewest(2));
            List<String> result = stringHistory.getFullRecord();
            assertNotNull(result);
            assertTrue(result.size() == 3);
            assertTrue(result.get(0).equals("E"));
            assertTrue(result.get(1).equals("F"));
            assertTrue(result.get(2).equals("G"));

            List<SyncOpItem<String>> opItems = stringHistory.getIncrementRecord(1);
            assertNotNull(opItems);
            assertTrue(opItems.size() == 5);
            assertEquals("C", opItems.get(0).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(0).getOp());

            assertEquals("E", opItems.get(1).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(1).getOp());

            assertEquals("F", opItems.get(2).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(2).getOp());

            assertEquals("B", opItems.get(3).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(3).getOp());

            assertEquals("G", opItems.get(4).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(4).getOp());

            opItems = stringHistory.getIncrementRecord(2);
            assertNotNull(opItems);
            assertTrue(opItems.size() == 3);
            assertEquals("B", opItems.get(0).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(0).getOp());

            assertEquals("D", opItems.get(1).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(1).getOp());

            assertEquals("G", opItems.get(2).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(2).getOp());
        }

        {
            // test exception
            try {
                stringHistory.getIncrementRecord(3);
                assertTrue(false);
            }catch (RuntimeException e){
                assertTrue(e instanceof SyncVersionException);
            }
        }

        {
            // test version merge
            SyncOpVersionRepo.MAX_KEEP_HISTORY_LEN = 3;
            SyncOpVersionRepo.MIN_MERGE_COUNT = 1;
            List<SyncOpItem<String>> itemList = new ArrayList<>();
            itemList.add(new SyncOpItem(SyncOpEnum.ADD, "H"));
            stringHistory.addRecordOp(itemList);
            try {
                stringHistory.getIncrementRecord(0);
                assertTrue(false);
            }catch (RuntimeException e){
                assertTrue(e instanceof SyncVersionException);
            }
            List<SyncOpItem<String>> opItems = stringHistory.getIncrementRecord(1);
            assertTrue(opItems.size() == 4);
            assertEquals("E", opItems.get(0).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(0).getOp());

            assertEquals("F", opItems.get(1).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(1).getOp());

            assertEquals("G", opItems.get(2).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(2).getOp());

            assertEquals("H", opItems.get(3).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(3).getOp());

            opItems = stringHistory.getIncrementRecord(2);
            assertTrue(opItems.size() == 4);
            assertEquals("B", opItems.get(0).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(0).getOp());

            assertEquals("D", opItems.get(1).getData());
            assertEquals(SyncOpEnum.DELETE, opItems.get(1).getOp());

            assertEquals("G", opItems.get(2).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(2).getOp());

            assertEquals("H", opItems.get(3).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(3).getOp());

            opItems = stringHistory.getIncrementRecord(3);
            assertTrue(opItems.size() == 1);
            assertEquals("H", opItems.get(0).getData());
            assertEquals(SyncOpEnum.ADD, opItems.get(0).getOp());

            List<String> result = stringHistory.getFullRecord();
            assertTrue(result.size() == 4);
            assertEquals("E", result.get(0));
            assertEquals("F", result.get(1));
            assertEquals("G", result.get(2));
            assertEquals("H", result.get(3));
        }
    }
}
