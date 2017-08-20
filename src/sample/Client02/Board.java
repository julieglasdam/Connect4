package sample.Client02;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julieglasdam on 03/04/2017.
 */
public class Board {
    private List<Integer> column00;
    private List<Integer> column01;
    private List<Integer> column02;
    private List<Integer> column03;


    public Board(){
        column00 = new ArrayList<>();
        column01 = new ArrayList<>();
        column02 = new ArrayList<>();
        column03 = new ArrayList<>();

        initLists(column00);
        initLists(column01);
        initLists(column02);
        initLists(column03);
    }

    // Takes a list as a parameter, and sets 4 indexes to 0
    private void initLists(List<Integer> list) {
        for (int i = 0; i < 4; i++) {
            list.add(0);
        }
    }

    /* Takes an integer as parameter and checks the column with that number to see if
    * it contains 0. If it does, the column (list) is not full and returns false.
    * If it doesn't, the column is filled up with pieces and returns true*/
    // Skal kortes ned med en multi arraylist
    public boolean columnIsFull(int column) {
        if (column == 0) {
            for (int i = 0; i < column00.size(); i++) {
                if (column00.contains(0)) {
                    return false;
                }
            }
        }
        else if (column == 1) {
            for (int i = 0; i < column01.size(); i++) {
                if (column01.contains(0)) {
                    return false;
                }
            }
        }
        else if (column == 2) {
            for (int i = 0; i < column02.size(); i++) {
                if (column02.contains(0)) {
                    return false;
                }
            }
        }
        else if (column == 3) {
            for (int i = 0; i < column03.size(); i++) {
                if (column03.contains(0)) {
                    return false;
                }
            }
        }

        return true;
    }

    /* Takes an int as parameter and checks that column, to see if it contains only
    one color (only the players color). If it contains 0 or 2, it doesn't contain only
    the players color and returns false. Else it returns true */
    public boolean columnContainsOnlyOneColor(int column) {
        if (column == 0) {
            for (int i = 0; i < column00.size(); i++) {
                if (column00.contains(0) || column00.contains(2)) {
                    return false;
                }
            }
        }

        if (column == 1) {
            for (int i = 0; i < column01.size(); i++) {
                if (column01.contains(0) || column01.contains(2)) {
                    return false;
                }
            }
        }

        if (column == 2) {
            for (int i = 0; i < column02.size(); i++) {
                if (column02.contains(0) || column02.contains(2)) {
                    return false;
                }
            }
        }

        if (column == 3) {
            for (int i = 0; i < column03.size(); i++) {
                if (column03.contains(0) || column03.contains(2)) {
                    return false;
                }
            }
        }


        return true;
    }

    /* Takes an int as parameter and checks each list (column), if that index contains
    * anything but 1. If it does, that row can't contain only the players color
    * and the method returns false. Else return true */
    public boolean rowContainsOnlyOneColor(int row) {
        if (column00.get(row) == 0 || column00.get(row) == 2) {
            return false;
        }
        else if (column01.get(row) == 0 || column01.get(row) == 2) {
            return false;
        }
        else if (column02.get(row) == 0 || column02.get(row) == 2) {
            return false;
        }
        if (column03.get(row) == 0 || column03.get(row) == 2) {
            return false;
        }
        return true;
    }

    /* Checks through all the lists (columns), and checks if their indexes contains anything
    * but 1, in the pattern of a diagonal path. If it coes, the path doesn't contain
    * only one color, and the method returns false. Else return true*/
    public boolean diagonalOnlyContainsOneColorLeftToRight() { // From left corner
        if (column00.get(0) == 0 || column00.get(0) == 2) {
            return false;
        }
        else if (column01.get(1) == 0 || column01.get(1) == 2) {
            return false;
        }
        else if (column02.get(2) == 0 || column02.get(2) == 2) {
            return false;
        }
        else if (column03.get(3) == 0 || column03.get(3) == 2) {
            return false;
        }

        return true;
    }

    public boolean diagonalOnlyContainsOneColorRightToLeft() { // From right corner
        // Check from right corner
        if (column00.get(3) == 0 || column00.get(3) == 2) {
            return false;
        }
        else if (column01.get(2) == 0 || column01.get(2) == 2) {
            return false;
        }
        else if (column02.get(1) == 0 || column02.get(1) == 2) {
            return false;
        }
        else if (column03.get(0) == 0 || column03.get(0) == 2) {
            return false;
        }
        return true;
    }


    /* Takes a list as a parameter and iterates through the list, to find the first index
    * with a 0 as value. The loop iterates backwards because the last index of the array
    * is represented as the circle in the bottom of the board. The method should only be
    * called inside an if-statement checking if columnIsFull returns false, or the program
     * will throw an array out of bound exception */
    public int firstEmptySpot(List<Integer> column) {
        for (int i = column.size()-1; i >= 0; i--) {
            if (column.get(i) == 0) {
                return i;
            }
        }
        return -1;
    }




    public List<Integer> getColumn00() {
        return column00;
    }

    public void setColumn00(int index, int value) {
        this.column00.add(index, value);
    }

    public List<Integer> getColumn01() {
        return column01;
    }

    public void setColumn01(int index, int value) {
        this.column01.add(index, value);
    }

    public List<Integer> getColumn02() {
        return column02;
    }

    public void setColumn02(int index, int value) {
        this.column02.add(index, value);
    }

    public List<Integer> getColumn03() {
        return column03;
    }

    public void setColumn03(int index, int value) {
        this.column03.add(index, value);
    }



}
