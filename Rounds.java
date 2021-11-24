package com.dropkick.btd3.Helpers;

import java.util.ArrayList;

public class Rounds {

    private final ArrayList<String[]> bloonsInRound = new ArrayList<>();
    private final ArrayList<int[]> amountofBloonsInRound = new ArrayList<>();

    public Rounds() {//bloonCount of each bloon is reduced by one to account for arrays starting at 0
        //bloonsInRound.add(new String[]{"Red"});//1 is done in GameScreen
        //amountofBloonsInRound.add(new int[]{13});
        bloonsInRound.add(new String[]{"Red"});//2
        amountofBloonsInRound.add(new int[]{29});
        bloonsInRound.add(new String[]{"Red", "Blue", "Red", "Blue"});//3
        amountofBloonsInRound.add(new int[]{9, 3, 4, 5});
        bloonsInRound.add(new String[]{"Red", "Blue", "Red", "Blue"});//4
        amountofBloonsInRound.add(new int[]{4, 11, 4, 11});
        bloonsInRound.add(new String[]{"Red", "Blue", "Red", "Blue"});//5
        amountofBloonsInRound.add(new int[]{9, 7, 11, 19});
        bloonsInRound.add(new String[]{"Red", "Green"});//6
        amountofBloonsInRound.add(new int[]{13, 15});
        bloonsInRound.add(new String[]{"Blue"});//7
        amountofBloonsInRound.add(new int[]{49});
        bloonsInRound.add(new String[]{"Red", "Blue", "Red", "Blue", "Red", "Blue"});//8
        amountofBloonsInRound.add(new int[]{8, 15, 8, 2, 8, 6});
        bloonsInRound.add(new String[]{"Blue", "Green", "Blue"});//9
        amountofBloonsInRound.add(new int[]{21, 15, 11});
        bloonsInRound.add(new String[]{"Green"});//10
        amountofBloonsInRound.add(new int[]{29});
        bloonsInRound.add(new String[]{"Green", "Yellow"});//11
        amountofBloonsInRound.add(new int[]{11, 11});
        bloonsInRound.add(new String[]{"Rainbow"});//12
        amountofBloonsInRound.add(new int[]{0});
        bloonsInRound.add(new String[]{"Blue", "Red", "Green", "Blue"});//13
        amountofBloonsInRound.add(new int[]{14, 14, 24, 14});
        bloonsInRound.add(new String[]{"Rainbow", "Yellow"});//14
        amountofBloonsInRound.add(new int[]{0, 14});
        bloonsInRound.add(new String[]{"Yellow", "Green", "Yellow", "Green", "Yellow"});//15
        amountofBloonsInRound.add(new int[]{8, 7, 6, 5, 4});
        bloonsInRound.add(new String[]{"Green", "Yellow", "Blue", "Yellow"});//16
        amountofBloonsInRound.add(new int[]{25, 14, 7, 6});
        bloonsInRound.add(new String[]{"Blue", "Green", "Yellow"});//17
        amountofBloonsInRound.add(new int[]{14, 39, 9});
        bloonsInRound.add(new String[]{"Blue", "Yellow", "Green"});//18
        amountofBloonsInRound.add(new int[]{24, 19, 24});
        bloonsInRound.add(new String[]{"Green", "Yellow"});//19
        amountofBloonsInRound.add(new int[]{29, 24});
        bloonsInRound.add(new String[]{"Lead"});//20
        amountofBloonsInRound.add(new int[]{5});
        bloonsInRound.add(new String[]{"Yellow", "Blue", "Yellow", "Green"});//21
        amountofBloonsInRound.add(new int[]{14, 9, 21, 29});
        bloonsInRound.add(new String[]{"Yellow"});//22
        amountofBloonsInRound.add(new int[]{34});
        bloonsInRound.add(new String[]{"Yellow", "Green", "Yellow"});//23
        amountofBloonsInRound.add(new int[]{19, 24, 21});
        bloonsInRound.add(new String[]{"Green", "Yellow", "Green", "Blue"});//24
        amountofBloonsInRound.add(new int[]{21, 29, 21, 17});
        bloonsInRound.add(new String[]{"Yellow", "Black", "Yellow"});//25
        amountofBloonsInRound.add(new int[]{19, 13, 19});
        bloonsInRound.add(new String[]{"Yellow", "Black", "Yellow", "Lead"});//26
        amountofBloonsInRound.add(new int[]{39, 14, 14, 13});
        bloonsInRound.add(new String[]{"Black"});//27
        amountofBloonsInRound.add(new int[]{29});
        bloonsInRound.add(new String[]{"Black", "White"});//28
        amountofBloonsInRound.add(new int[]{15, 18});
        bloonsInRound.add(new String[]{"Lead", "Black", "White"});//29
        amountofBloonsInRound.add(new int[]{5, 7, 11});
        bloonsInRound.add(new String[]{"Yellow", "Black"});//30
        amountofBloonsInRound.add(new int[]{54, 27});
        bloonsInRound.add(new String[]{"Ceramic"});//31
        amountofBloonsInRound.add(new int[]{1});
        bloonsInRound.add(new String[]{"Yellow", "White", "Black"});//32
        amountofBloonsInRound.add(new int[]{19, 14, 19});
        bloonsInRound.add(new String[]{"Black", "Ceramic"});//33
        amountofBloonsInRound.add(new int[]{54, 2});
        bloonsInRound.add(new String[]{"Black", "White", "Yellow", "Ceramic"});//34
        amountofBloonsInRound.add(new int[]{19, 19, 49, 3});
        bloonsInRound.add(new String[]{"Rainbow"});//35
        amountofBloonsInRound.add(new int[]{11});
        bloonsInRound.add(new String[]{"Black", "Yellow", "Black", "Lead", "White", "Black"});//36
        amountofBloonsInRound.add(new int[]{9, 14, 11, 11, 11, 9});
        bloonsInRound.add(new String[]{"M.O.A.B"});//37
        amountofBloonsInRound.add(new int[]{0});
        bloonsInRound.add(new String[]{"Ceramic", "Yellow", "Black", "Ceramic"});//38
        amountofBloonsInRound.add(new int[]{0, 54, 39, 3});
        bloonsInRound.add(new String[]{"Yellow", "Black", "White", "Lead", "Rainbow", "White"});//39
        amountofBloonsInRound.add(new int[]{29, 19, 9, 9, 9, 9});
        bloonsInRound.add(new String[]{"Black", "Ceramic"});//40
        amountofBloonsInRound.add(new int[]{59, 4});
        bloonsInRound.add(new String[]{"White", "Lead", "Ceramic"});//41
        amountofBloonsInRound.add(new int[]{19, 11, 13});
        bloonsInRound.add(new String[]{"Ceramic", "Yellow", "Black"});//42
        amountofBloonsInRound.add(new int[]{7, 59, 14});
        bloonsInRound.add(new String[]{"Rainbow", "Lead", "Ceramic"});//43
        amountofBloonsInRound.add(new int[]{14, 19, 4});
        bloonsInRound.add(new String[]{"Ceramic", "Black", "M.O.A.B"});//44
        amountofBloonsInRound.add(new int[]{4, 79, 0});
        bloonsInRound.add(new String[]{"Ceramic", "White"});//45
        amountofBloonsInRound.add(new int[]{9, 59});
        bloonsInRound.add(new String[]{"Rainbow", "Ceramic", "Lead", "Rainbow"});//46
        amountofBloonsInRound.add(new int[]{4, 4, 34, 11});
        bloonsInRound.add(new String[]{"Black", "Ceramic", "Lead", "Rainbow", "Ceramic"});//47
        amountofBloonsInRound.add(new int[]{29, 4, 11, 7, 4});
        bloonsInRound.add(new String[]{"Rainbow", "White", "Black", "Lead", "Rainbow"});//48
        amountofBloonsInRound.add(new int[]{11, 19, 19, 7, 7});
        bloonsInRound.add(new String[]{"Ceramic", "Rainbow", "Ceramic"});//49
        amountofBloonsInRound.add(new int[]{3, 19, 11});
        bloonsInRound.add(new String[]{"Ceramic", "Rainbow", "Lead", "Ceramic", "Rainbow", "Lead", "Rainbow", "M.O.A.B"});//50
        amountofBloonsInRound.add(new int[]{5, 9, 6, 4, 5, 5, 8, 3, 8, 1});
    }
    
    public String[] getBloonListForRound(int i) {
        return bloonsInRound.get(i);
    }
    
    public int[] getBloonCountForRound(int i) {
        return amountofBloonsInRound.get(i);
    }
}
