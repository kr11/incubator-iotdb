package org.apache.iotdb.db.utils.datastructure.primitive;

import org.apache.iotdb.db.exception.metadata.IllegalPathException;
import org.apache.iotdb.db.qp.physical.crud.QueryIndexPlan;
import org.apache.iotdb.db.qp.physical.crud.QueryPlan;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanPrimitiveListTest {

  @Test
  public void setBoolean() throws IllegalPathException {
    QueryIndexPlan plan = new QueryIndexPlan();
    //    System.out.println(plan instanceof QueryIndexPlan);
    System.out.println(plan instanceof QueryPlan);
    //    PartialPath p = new PartialPath("asd.asd.ew.17238192");
    //    System.out.println(Arrays.toString(p.getNodes()));
    //    PrimitiveList booleanList = PrimitiveList.newList(TSDataType.BOOLEAN);
    //    System.out.println(String.format("%b,", true));
    //    System.out.println(String.format("%b,", false));
    //    booleanList.setBoolean(0, true);
    //    booleanList.setBoolean(1, true);
    //    booleanList.setBoolean(8, true);
    //    Assert.assertEquals("{true,true,false,false,false,false,false,false,true,}",
    // booleanList.toString());
    //    System.out.println(booleanList);
  }
}