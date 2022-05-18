package ui.algorithm;

import ui.dataset.Dataset;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public abstract class Algorithm {

  public abstract void fit(Dataset dataset);

  public abstract void predict(Dataset dataset);

}
