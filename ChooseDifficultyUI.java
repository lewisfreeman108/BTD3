package com.dropkick.btd3.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dropkick.btd3.BTD3;
import com.dropkick.btd3.Helpers.UICreator;
import com.dropkick.btd3.Screens.GameScreen;

public class ChooseDifficultyUI extends Stage {

    public boolean chosen;

    public ChooseDifficultyUI(final BTD3 btd3) {
        Gdx.input.setInputProcessor(this);
        FreeTypeFontGenerator arial = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Arial/arial.ttf"));
        UICreator uiCreator = new UICreator();
        BitmapFont arialBig = uiCreator.createFont(arial, Color.WHITE, Gdx.graphics.getWidth() / 25 * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        String[] label = new String[]{"Easy", "Medium", "Hard"};
        for(int i = 0 ; i < 3 ; i++) {
            final Integer ii = i;
            TextButton button = uiCreator.createTextButton(arialBig, label[i], Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 15f, Gdx.graphics.getWidth() * (1f + i * 2f) / 6 - Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() / 2f - Gdx.graphics.getHeight() / 30f,
                    new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            btd3.setScreen(new GameScreen(btd3.getBatch(), ii));
                            chosen = true;
                            return true;
                        }
                    });
            button.setDebug(true);
            addActor(button);
        }
    }
}
