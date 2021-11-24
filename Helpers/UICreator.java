package com.dropkick.btd3.Helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UICreator {

    public BitmapFont createFont(FreeTypeFontGenerator generator, Color color, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size = size;
        return generator.generateFont(parameter);
    }

    //Buttons

    public TextButton createTextButton(BitmapFont font, String text, float width, float height, float x, float y, ClickListener listener) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        TextButton button = new TextButton(text, style);
        button.setPosition(x, y);
        button.setSize(width, height);
        button.addListener(listener);
        return button;
    }
}
