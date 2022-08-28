package machimania.input;

import basewindow.BaseFile;
import basewindow.InputCodes;

public class InputBindings
{
    public BaseFile file;

    public InputBindingGroup moveUp = new InputBindingGroup("up", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_UP));
    public InputBindingGroup moveDown = new InputBindingGroup("down", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_DOWN));
    public InputBindingGroup moveLeft = new InputBindingGroup("left", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_LEFT));
    public InputBindingGroup moveRight = new InputBindingGroup("right", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_RIGHT));
    public InputBindingGroup moveJump = new InputBindingGroup("jump", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_SPACE));
    public InputBindingGroup sprint = new InputBindingGroup("sprint", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_RIGHT_SHIFT));
    public InputBindingGroup advance = new InputBindingGroup("advance", new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_ENTER), new InputBinding(InputBinding.InputType.keyboard, InputCodes.KEY_SPACE));

}
