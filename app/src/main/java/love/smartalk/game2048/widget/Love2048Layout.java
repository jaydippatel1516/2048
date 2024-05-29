package love.smartalk.game2048.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Love2048Layout extends RelativeLayout {

    private int column = 4;
    private Item2048[] item2048s;
    private int margin = 10;
    private int padding;

    private GestureDetector gestureDetector;
    private boolean isMerged = true;
    private boolean isMoved = true;
    private boolean once;
    private int score;
    final int FLING_MIN_DISTANCE = 100;

    private enum ACTION {
        LEFT, RIGHT, UP, DOWN
    }

    public interface OnLove2048Listener {
        void onScoreChanged(int score);

        //void onGameSucceed();
        void onGameOver();
    }

    public void setLove2048Listener(OnLove2048Listener love2048Listener) {
        this.love2048Listener = love2048Listener;
    }

    private OnLove2048Listener love2048Listener;


    public Love2048Layout(Context context) {
        this(context, null);
    }

    public Love2048Layout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Love2048Layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                margin,
                getResources().getDisplayMetrics());
        padding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
        gestureDetector = new GestureDetector(getContext(), new MyGestureDetector());
    }

    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (min > param)
                min = param;
        }
        return min;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int game2048LayoutSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
        int item2048Size = (game2048LayoutSize - padding * 2 - margin * (column - 1)) / column;
        if (!once) {
            if (item2048s == null)
                item2048s = new Item2048[column * column];

            for (int i = 0; i < item2048s.length; i++) {
                Item2048 item2048 = new Item2048(getContext());
                item2048s[i] = item2048;
                item2048.setId(i + 1);
                RelativeLayout.LayoutParams layoutParams = new LayoutParams(item2048Size, item2048Size);

                if ((i + 1) % column != 0)
                    layoutParams.rightMargin = margin;


                if (i % column != 0)
                    layoutParams.addRule(RIGHT_OF, item2048s[i - 1].getId());


                if (i + 1 > column) {
                    layoutParams.topMargin = margin;
                    layoutParams.addRule(BELOW, item2048s[i - column].getId());
                }
                addView(item2048, layoutParams);
            }
            generateNum();
        }
        once = true;
        setMeasuredDimension(game2048LayoutSize, game2048LayoutSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void action(ACTION action) {
        for (int i = 0; i < column; i++) {
            List<Item2048> item2048Row = new ArrayList<>();
            for (int j = 0; j < column; j++) {
                int index = getIndexByAction(action, i, j);
                Item2048 item2048 = item2048s[index];
                if (item2048.getNumber() != 0)
                    item2048Row.add(item2048);
            }

            for (int j = 0; j < column && j < item2048Row.size(); j++) {
                int index = getIndexByAction(action, i, j);
                Item2048 item = item2048s[index];
                if (item.getNumber() != item2048Row.get(j).getNumber())
                    isMoved = true;
            }

            mergeItem(item2048Row);

            for (int j = 0; j < column; j++) {
                int index = getIndexByAction(action, i, j);
                if (item2048Row.size() > j) {
                    item2048s[index].setNumber(item2048Row.get(j).getNumber());
                } else {
                    item2048s[index].setNumber(0);
                }
            }
        }
        generateNum();
    }

    private int getIndexByAction(ACTION action, int i, int j) {
        int index = -1;
        switch (action) {
            case LEFT:
                index = i * column + j;
                break;
            case RIGHT:
                index = i * column + column - j - 1;
                break;
            case UP:
                index = i + j * column;
                break;
            case DOWN:
                index = i + (column - 1 - j) * column;
                break;
        }
        return index;
    }


    private void mergeItem(List<Item2048> row) {
        if (row.size() < 2)
            return;

        for (int j = 0; j < row.size() - 1; j++) {
            Item2048 item1 = row.get(j);
            Item2048 item2 = row.get(j + 1);

            if (item1.getNumber() == item2.getNumber()) {
                isMerged = true;

                int val = item1.getNumber() + item2.getNumber();
                item1.setNumber(val);

                score += val;
                if (love2048Listener != null) {
                    love2048Listener.onScoreChanged(score);
                }

                for (int k = j + 1; k < row.size() - 1; k++) {
                    row.get(k).setNumber(row.get(k + 1).getNumber());
                }

                row.get(row.size() - 1).setNumber(0);
                return;
            }
        }


    }


    private boolean isFull() {
        // 检测是否所有位置都有数字
        for (int i = 0; i < item2048s.length; i++) {
            if (item2048s[i].getNumber() == 0)
                return false;
        }
        return true;
    }


    private boolean checkOver() {

        if (!isFull()) {
            return false;
        }

        for (int i = 0; i < column; i++) {
            for (int j = 0; j < column; j++) {

                int index = i * column + j;


                Item2048 item = item2048s[index];

                if ((index + 1) % column != 0) {

                    Item2048 itemRight = item2048s[index + 1];
                    if (item.getNumber() == itemRight.getNumber())
                        return false;
                }

                if ((index + column) < column * column) {
                    Item2048 itemBottom = item2048s[index + column];
                    if (item.getNumber() == itemBottom.getNumber())
                        return false;
                }

                if (index % column != 0) {
                    Item2048 itemLeft = item2048s[index - 1];
                    if (itemLeft.getNumber() == item.getNumber())
                        return false;
                }

                if (index + 1 > column) {
                    Item2048 itemTop = item2048s[index - column];
                    if (item.getNumber() == itemTop.getNumber())
                        return false;
                }

            }

        }

        return true;

    }

    public void generateNum() {

        if (checkOver()) {
            if (love2048Listener != null) {
                love2048Listener.onGameOver();
            }
            return;
        }

        if (!isFull()) {
            if (isMoved || isMerged) {
                Random random = new Random();
                int next = random.nextInt(16);
                Item2048 item = item2048s[next];

                while (item.getNumber() != 0) {
                    next = random.nextInt(16);
                    item = item2048s[next];
                }

                item.setNumber(Math.random() > 0.75 ? 4 : 2);
                item.scaleIn();
                isMerged = isMoved = false;
            }

        }
    }


    public void restart() {
        for (Item2048 item : item2048s) {
            item.setNumber(0);
        }
        score = 0;
        if (love2048Listener != null) {
            love2048Listener.onScoreChanged(score);
        }
        isMoved = isMerged = true;
        generateNum();
    }


    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float moveX = e2.getX() - e1.getX();
            float moveY = e2.getY() - e1.getY();

            if (moveX > FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                action(ACTION.RIGHT);
            } else if (moveX < -FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                action(ACTION.LEFT);
            } else if (moveY > FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                action(ACTION.DOWN);
            } else if (moveY < -FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                action(ACTION.UP);
            }
            return true;
        }


    }

}
