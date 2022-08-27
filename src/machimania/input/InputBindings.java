package machimania.input;

import basewindow.BaseFile;
import basewindow.InputCodes;

public class InputBindings
{
    public BaseFile file;

    public InputBindingGroup moveUp = new InputBindingGroup("up", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_UP), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_W));
    public InputBindingGroup moveDown = new InputBindingGroup("down", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_DOWN), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_S));
    public InputBindingGroup moveLeft = new InputBindingGroup("left", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_LEFT), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_A));
    public InputBindingGroup moveRight = new InputBindingGroup("right", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_RIGHT), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_D));

    public InputBindingGroup advance = new InputBindingGroup("advance", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_ENTER), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_SPACE));

}
