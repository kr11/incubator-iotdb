package org.apache.iotdb.db.index.algorithm.elb.feature;


import java.util.Arrays;
import org.apache.iotdb.db.index.algorithm.elb.pattern.MilesPattern;

/**
 * Calculate the upper and lower envelope. Actually, it's only used by {@linkplain
 * ElementELBFeature}.
 */
public class PatternEnvelope {

  double[] upperLine;
  double[] valueLine;
  double[] lowerLine;

  public void refresh(MilesPattern pattern) {
    if (upperLine == null || upperLine.length < pattern.subpatternCount) {
      upperLine = new double[pattern.sequenceLen];
      valueLine = new double[pattern.sequenceLen];
      lowerLine = new double[pattern.sequenceLen];
    }
    Arrays.fill(upperLine, -Double.MAX_VALUE);
    Arrays.fill(lowerLine, Double.MAX_VALUE);
    Arrays.fill(valueLine, 0);
    refreshPattern(pattern);
  }

  private void refreshPattern(MilesPattern pattern) {
    int i;
    double tol;
    //i, i.e. k in the paper.
    // region of variable border，[s_k,e_k)
    for (i = 0; i < pattern.subpatternCount; i++) {
      for (int j = pattern.minLeftBorders[i]; j < pattern.maxLeftBorders[i]; j++) {
        double left = mtt(pattern, i - 1);
        double right = mtt(pattern, i);
        tol = Math.max(left, right);
        upperLine[j] = pattern.getDoubleFromRelativeIdx(j) + tol;
        valueLine[j] = pattern.getDoubleFromRelativeIdx(j);
        lowerLine[j] = pattern.getDoubleFromRelativeIdx(j) - tol;
      }
      // region of non-variable border ，[e_k,s_k+1)
      int m =
          (i + 1 == pattern.subpatternCount) ? pattern.sequenceLen : pattern.minLeftBorders[i + 1];
      for (int j = pattern.maxLeftBorders[i]; j < m; j++) {
        tol = mtt(pattern, i);
        upperLine[j] = pattern.getDoubleFromRelativeIdx(j) + tol;
        valueLine[j] = pattern.getDoubleFromRelativeIdx(j);
        lowerLine[j] = pattern.getDoubleFromRelativeIdx(j) - tol;
      }
    }
  }

  private double mtt(MilesPattern pattern, int k) {
    return Math.pow((double) pattern.maxLeftBorders[k + 1] - pattern.minLeftBorders[k],
        1 / pattern.distance.getP())
        * pattern.thresholdsArray[k];
  }

}