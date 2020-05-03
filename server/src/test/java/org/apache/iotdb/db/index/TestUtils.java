package org.apache.iotdb.db.index;

import static org.apache.iotdb.db.index.common.IndexConstant.INDEXED_SUFFIX;
import static org.apache.iotdb.db.index.common.IndexConstant.INDEXING_SUFFIX;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import org.apache.iotdb.db.index.algorithm.RTree;
import org.apache.iotdb.db.index.common.IndexType;
import org.apache.iotdb.db.index.io.IndexIOReader;
import org.apache.iotdb.db.index.io.IndexIOWriter.IndexChunkMeta;
import org.apache.iotdb.db.index.preprocess.Identifier;
import org.apache.iotdb.db.utils.datastructure.TVList;
import org.apache.iotdb.tsfile.exception.NotImplementedException;
import org.apache.iotdb.tsfile.fileSystem.FSFactoryProducer;
import org.apache.iotdb.tsfile.read.TimeValuePair;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.utils.Pair;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;
import org.junit.Assert;

public class TestUtils {

  public static String TEST_INDEX_FILE_NAME = "test_index";

  public static void clearIndexFile(String index_name) {
    FSFactoryProducer.getFSFactory().getFile(index_name).delete();
    FSFactoryProducer.getFSFactory().getFile(index_name + INDEXED_SUFFIX).delete();
    FSFactoryProducer.getFSFactory().getFile(index_name + INDEXING_SUFFIX).delete();
  }

  public static String tvListToString(TVList tvList) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (int i = 0; i < tvList.size(); i++) {
      TimeValuePair pair = tvList.getTimeValuePair(i);
      switch (tvList.getDataType()) {
        case INT32:
          sb.append(String.format("[%d,%d],", pair.getTimestamp(), pair.getValue().getInt()));
          break;
        case INT64:
          sb.append(String.format("[%d,%d],", pair.getTimestamp(), pair.getValue().getLong()));
          break;
        case FLOAT:
          sb.append(String.format("[%d,%.2f],", pair.getTimestamp(), pair.getValue().getFloat()));
          break;
        case DOUBLE:
          sb.append(String.format("[%d,%.2f],", pair.getTimestamp(), pair.getValue().getDouble()));
          break;
        default:
          throw new NotImplementedException(tvList.getDataType().toString());
      }
    }
    sb.append("}");
    return sb.toString();
  }

  private static String doubleToFormatStringCoverInf(double d) {
    if (d == Double.MAX_VALUE) {
      return "Inf";
    } else if (d == -Double.MAX_VALUE) {
      return "-Inf";
    } else {
      return String.format("%.2f", d);
    }
  }

  public static String TwoDimDoubleArrayToString(double[][] mbrs) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (double[] mbr : mbrs) {
      sb.append(String.format("[%s,%s],",
          doubleToFormatStringCoverInf(mbr[0]),
          doubleToFormatStringCoverInf(mbr[1])));
    }
    sb.append("}");
    return sb.toString();
  }

  public static class Validation {

    public String path;
    public TVList tvList;
    public List<Pair<IndexType, String>> gt;

    public Validation(String path, TVList tvList, List<Pair<IndexType, String>> gt) {
      this.path = path;
      this.tvList = tvList;
      this.gt = gt;
    }
  }

  public static String deserializeIndexChunk(IndexType indexType, ByteBuffer byteBuffer) {
    StringBuilder sb = new StringBuilder();
    switch (indexType) {
      case NO_INDEX:
        int size = ReadWriteIOUtils.readInt(byteBuffer);
        for (int i = 0; i < size; i++) {
          sb.append(Identifier.deserialize(byteBuffer));
        }
        return sb.toString();
      case PAA:
      case ELB:
        BiConsumer<Integer, ByteBuffer> deserial = (i, b) -> {
          Identifier id = Identifier.deserialize(b);
          sb.append(String.format("(%d,%s)", i, id.toString()));
        };
        RTree.deserialize(byteBuffer, deserial);
        return sb.toString();
      case KV_INDEX:
      default:
        throw new UnsupportedOperationException();
    }
  }

}