
package materials.ui;

import data.Material;

/**
 *
 * @author alan
 */
public interface MaterialDisplayInterface {

    void clear();

    Material getMaterial();

    void reportRrror(String theError);

    void setMaterial(Material material);

    public void disableDelete();

    public void disableSave();

    public void enableSave();

    public void enableDelete();
}
