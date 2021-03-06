package ian.hu.wechat.sdk.entity.menu.button;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by ian on 15/12/12.
 */
public class SubButton extends AbstractButton {

    @JsonProperty("sub_button")
    protected ArrayList<AbstractButton> subButtons = new ArrayList<AbstractButton>();

    public SubButton() {
    }

    public SubButton(String name) {
        this.setName(name);
    }

    public ArrayList<AbstractButton> getSubButtons() {
        return subButtons;
    }

    public void setSubButtons(ArrayList<AbstractButton> subButtons) {
        this.subButtons = subButtons;
    }

    public Boolean add(AbstractButton button) {
        if (subButtons.size() >= 5) {
            return false;
        }
        subButtons.add(button);
        return true;
    }

    @Override
    public String toString() {
        return "SubButton{" +
                "name=" + getName() +
                ",subButtons=" + subButtons +
                '}';
    }
}
