package tex.xb.com.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tex.xb.com.gesturelockview.R;

/**
 * Created by sxb on 2017/6/12 0012.
 */

public class GestureLockView extends View {

    //圆环半径
    private int cicleRadius;

    //圆环粗细
    private int cicleWidth;

    //圆环/线/中间圆默认状态下的颜色
    private int cicleNormalColor;

    //圆环/线/中间圆选中颜色
    private int cicleSelectColor;

    //错误时圆环/线/中间圆颜色
    private int cicleErrorColor;

    //背景圆颜色
    private int bgCicleColor;

    //线宽
    private int lineWidth;

    //内部圆的半径
    private int cellInnerRadius;

    //内部背景圆
    private int bgInnerRadius;

    // 圆形之间的间隔距离，
    private int cicleSpace;

    private boolean mInStealthMode = false;

    private boolean mEnableHapticFeedback = false;

    private boolean isActionMove = false;//滑动
    private boolean isActionDown = false;//按下
    private boolean isActionUp = true;//抬起
    private float movingX, movingY;

    private Paint normalPaint, selectPaint, errorPaint,bgselectPaint;

    private OnChooseListener chooseListener;

    private Cicle[][] mCells = new Cicle[3][3];
    private List<Cicle> sCells = new ArrayList<Cicle>();
    private Paint mLineNormalPaint,mLineCheckPaint,mLineErrorPaint;

    public GestureLockView(Context context) {
        this(context, null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureLockView, defStyleAttr, 0);
        cicleRadius = a.getDimensionPixelSize(R.styleable.GestureLockView_cicleRadius, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics()));
        cicleWidth = a.getDimensionPixelSize(R.styleable.GestureLockView_cicleWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        lineWidth = a.getDimensionPixelSize(R.styleable.GestureLockView_MylineWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));

        cellInnerRadius = a.getDimensionPixelSize(R.styleable.GestureLockView_innerRadius, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        bgInnerRadius = a.getDimensionPixelSize(R.styleable.GestureLockView_bginnerRadius, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        cicleSpace = a.getDimensionPixelSize(R.styleable.GestureLockView_MylineWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 39, getResources().getDisplayMetrics()));
        cicleNormalColor = a.getColor(R.styleable.GestureLockView_cicleNormal, getResources().getColor(R.color.color_66));
        cicleSelectColor = a.getColor(R.styleable.GestureLockView_cicleSelect, getResources().getColor(R.color.color_ffb837));
        cicleErrorColor = a.getColor(R.styleable.GestureLockView_cicleError, Color.RED);
        bgCicleColor = a.getColor(R.styleable.GestureLockView_bgCicleColor, getResources().getColor(R.color.color_f2c2));
        a.recycle();
        initPaint();
    }


    private void initPaint() {
        normalPaint = new Paint();
        normalPaint.setColor(cicleNormalColor);
        normalPaint.setStrokeWidth(cicleWidth);
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setAntiAlias(true);

        selectPaint = new Paint();
        selectPaint.setColor(cicleSelectColor);
        selectPaint.setStrokeWidth(cicleWidth);
        //selectPaint.setStyle(Style.STROKE);
        selectPaint.setAntiAlias(true);


        bgselectPaint = new Paint();
        bgselectPaint.setColor(bgCicleColor);
        bgselectPaint.setStrokeWidth(cicleWidth);
        bgselectPaint.setStyle(Paint.Style.FILL);
        bgselectPaint.setAntiAlias(true);

        errorPaint = new Paint();
        errorPaint.setColor(cicleErrorColor);
        errorPaint.setStrokeWidth(cicleWidth);
        //errorPaint.setStyle(Style.STROKE);
        errorPaint.setAntiAlias(true);


        mLineNormalPaint = new Paint();
        mLineNormalPaint.setColor(cicleNormalColor);
        mLineNormalPaint.setStrokeWidth(lineWidth);
        mLineNormalPaint.setStyle(Paint.Style.STROKE);
        mLineNormalPaint.setAntiAlias(true);

        mLineCheckPaint = new Paint();
        mLineCheckPaint.setColor(cicleSelectColor);
        mLineCheckPaint.setStrokeWidth(lineWidth);
        mLineCheckPaint.setStyle(Paint.Style.STROKE);
        mLineCheckPaint.setAntiAlias(true);

        mLineErrorPaint = new Paint();
        mLineErrorPaint.setColor(cicleErrorColor);
        mLineErrorPaint.setStrokeWidth(lineWidth);
        mLineErrorPaint.setStyle(Paint.Style.STROKE);
        mLineErrorPaint.setAntiAlias(true);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) {
            widthSize = cicleWidth + cicleRadius * 6 + cicleSpace * 2 + getPaddingLeft() + getPaddingRight();
        }
        mode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) {
            heightSize = cicleWidth + cicleRadius * 6 + cicleSpace * 2 + getPaddingTop() + getPaddingBottom();
        }


        int contentWidth = cicleWidth + cicleRadius * 6 + cicleSpace * 2;
        int contentHeight = cicleWidth + cicleRadius * 6 + cicleSpace * 2;

        //第一个圆的圆心坐标
        int firstCenterX = widthSize / 2 - contentWidth / 2 + cicleRadius + cicleWidth / 2;
        int firstCenterY = heightSize / 2 - contentHeight / 2 + cicleRadius + cicleWidth / 2;

        // 存储每个圆心的坐标
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mCells[i][j] = new Cicle(firstCenterX + j * (cicleRadius * 2 + cicleSpace),
                        firstCenterY + i * (cicleRadius * 2 + cicleSpace), i, j, 3 * i + j + 1);
            }
        }
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCanvas(canvas);
    }

    private void drawCanvas(Canvas canvas) {

        //画圆
        for (int i = 0; i < mCells.length; i++) {
            for (int j = 0; j < mCells[i].length; j++) {
                if (mCells[i][j].getStatus() == Cicle.STATE_CHECK) {
                    selectPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            cicleRadius, this.selectPaint);
                    selectPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.bgInnerRadius, this.bgselectPaint);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellInnerRadius, this.selectPaint);
                } else if (mCells[i][j].getStatus() == Cicle.STATE_NORMAL) {
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            cicleRadius, this.normalPaint);
                } else if (mCells[i][j].getStatus() == Cicle.STATE_CHECK_ERROR) {
                    errorPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            cicleRadius, this.errorPaint);
                    errorPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(mCells[i][j].getX(), mCells[i][j].getY(),
                            this.cellInnerRadius, this.errorPaint);
                }
            }
        }
        //选中的圆进行画线
        if (sCells.size() > 0) {
            Cicle tempCell = sCells.get(0);
            for (int i = 1; i < sCells.size(); i++) {
                Cicle cell = sCells.get(i);
                if (cell.getStatus() == Cicle.STATE_CHECK) {
                    drawLine(tempCell, cell, canvas, mLineCheckPaint);

                } else if (cell.getStatus() == Cicle.STATE_CHECK_ERROR) {
                    drawLine(tempCell, cell, canvas, mLineErrorPaint);
                }
                tempCell = cell;
            }

            if (isActionMove && !isActionUp) {
                this.drawLineFollowFinger(tempCell, canvas, mLineCheckPaint);
            }
        }
    }


    /**
     *
     *
     * @param preCell
     * @param nextCell
     * @param canvas
     * @param paint
     */
    private void drawLine(Cicle preCell, Cicle nextCell, Canvas canvas, Paint paint) {
        Cicle centerCell = getCellBetweenTwoCells(preCell, nextCell);
        if (centerCell != null && sCells.contains(centerCell)) {
            drawLineNotIncludeCircle(centerCell, preCell, canvas, paint);
            drawLineNotIncludeCircle(centerCell, nextCell, canvas, paint);
        } else {
            drawLineNotIncludeCircle(preCell, nextCell, canvas, paint);
        }
    }

    /**
     * 滑动画线
     *
     * @param preCell
     * @param canvas
     * @param paint
     */
    private void drawLineFollowFinger(Cicle preCell, Canvas canvas, Paint paint) {
        float distance = getDistanceBetweenTwoPoints(
                preCell.getX(), preCell.getY(), movingX, movingY);
        if (distance > (cellInnerRadius)) {
            float x1 = (cellInnerRadius) / distance * (movingX - preCell.getX()) + preCell.getX();
            float y1 = (cellInnerRadius) / distance * (movingY - preCell.getY()) + preCell.getY();
            canvas.drawLine(x1, y1, movingX, movingY, paint);
        }
    }

    /**
     * 从圆的边开始弧线
     *
     * @param preCell
     * @param nextCell
     * @param canvas
     * @param paint
     */
    private void drawLineNotIncludeCircle(Cicle preCell, Cicle nextCell, Canvas canvas, Paint paint) {
        float distance = getDistanceBetweenTwoPoints(
                preCell.getX(), preCell.getY(), nextCell.getX(), nextCell.getY());
        float x1 = cellInnerRadius / distance * (nextCell.getX() - preCell.getX()) + preCell.getX();
        float y1 = cellInnerRadius / distance * (nextCell.getY() - preCell.getY()) + preCell.getY();
        float x2 = (distance - cellInnerRadius) / distance *
                (nextCell.getX() - preCell.getX()) + preCell.getX();
        float y2 = (distance - cellInnerRadius) / distance *
                (nextCell.getY() - preCell.getY()) + preCell.getY();
        canvas.drawLine(x1, y1, x2, y2, paint);
    }


    /**
     * 计算两点距离
     * @param fpX
     * @param fpY
     * @param spX
     * @param spY
     * @return
     */
    public  float getDistanceBetweenTwoPoints(float fpX, float fpY, float spX, float spY) {
        return (float) Math.sqrt((spX - fpX) * (spX - fpX) + (spY - fpY) * (spY - fpY));
    }


    private Cicle getCellBetweenTwoCells(Cicle preCell, Cicle nextCell) {
        //two cells are in the same row
        if (preCell.getRow() == nextCell.getRow()) {
            if (Math.abs(nextCell.getColumn() - preCell.getColumn()) > 1) {
                return mCells[preCell.getRow()][1];
            } else {
                return null;
            }
        }
        //two cells are in the same column
        else if (preCell.getColumn() == nextCell.getColumn()) {
            if (Math.abs(nextCell.getRow() - preCell.getRow()) > 1) {
                return mCells[1][preCell.getColumn()];
            } else {
                return null;
            }
        }
        //opposite angles
        else if (Math.abs(nextCell.getColumn() - preCell.getColumn()) > 1
                && Math.abs(nextCell.getRow() - preCell.getRow()) > 1) {
            return mCells[1][1];
        } else {
            return null;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float ex = event.getX();
        float ey = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(ex, ey);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(ex, ey);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp();
                break;
        }
        return true;
    }

    private void handleActionDown(float ex, float ey) {
        isActionMove = false;
        isActionDown = true;
        isActionUp = false;

        //按下时将之前选择的清空
        if (sCells != null && sCells.size() > 0) {
            for (Cicle cell : sCells) {
                cell.setStatus(Cicle.STATE_NORMAL);
            }
            sCells.clear();
        }
        Cicle cell = checkSelectCell(ex, ey);
        if (cell != null) {
            addSelectedCell(cell);
        }
        this.postInvalidate();
    }

    private void handleActionMove(float ex, float ey) {
        isActionMove = true;
        movingX = ex;
        movingY = ey;
        Cicle cell = checkSelectCell(ex, ey);
        if (cell != null) {
            addSelectedCell(cell);

        }
        this.postInvalidate();
    }


    private void handleActionUp() {
        isActionMove = false;
        isActionUp = true;
        isActionDown = false;
        if (this.chooseListener != null) {

            if(sCells.size()<4){
                this.chooseListener.onMaxNum();
            }else {
                StringBuffer stringBuffer = new StringBuffer();
                for (Cicle cell : sCells) {
                    stringBuffer.append(cell.getIndex());
                }

                this.chooseListener.onComplete(stringBuffer.toString());
            }


        }
        this.postInvalidate();
    }

    private void addSelectedCell(Cicle cell) {
        if (!sCells.contains(cell)) {
            cell.setStatus(Cicle.STATE_CHECK);
            // handleHapticFeedback();
            sCells.add(cell);
        }

    }

    //获取点所在的圆
    private Cicle checkSelectCell(float x, float y) {
        for (int i = 0; i < mCells.length; i++) {
            for (int j = 0; j < mCells[i].length; j++) {
                Cicle cell = mCells[i][j];
                if (checkInRound(cell.x, cell.y, cicleRadius, x, y)) {
                    return cell;
                }
            }
        }
        return null;
    }


    /**
     * 勾股计算两点间的距离
     * 小于半径即在圆中
     *
     * @param x1
     * @param y1
     * @param r
     * @param x
     * @param y
     * @return
     */
    public static boolean checkInRound(float x1, float y1, float r, float x, float y) {
        return Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)) < r;
    }



    public void setError(){
        if (sCells != null && sCells.size() > 0) {
            for (Cicle cell : sCells) {
                cell.setStatus(Cicle.STATE_CHECK_ERROR);
            }
            this.invalidate();
        }

    }


    public void clear(){
        if (sCells != null && sCells.size() > 0) {
            for (Cicle cell : sCells) {
                cell.setStatus(Cicle.STATE_NORMAL);
            }
            sCells.clear();
            this.invalidate();
        }
    }


    public void setOnChooseListener(OnChooseListener chooseListener) {
        this.chooseListener = chooseListener;
    }



    public  interface OnChooseListener {
         void onMaxNum();

         void onComplete(String pwd);
    }


    public class Cicle {
        private int x;// 圆心坐标x
        private int y;// 圆心坐标y
        private int row;// 行
        private int column;// 列
        private int index;// 密码
        private int status = 0;


        public static final int STATE_NORMAL = 0;

        public static final int STATE_CHECK = 1;

        public static final int STATE_CHECK_ERROR = 2;

        public Cicle() {
        }

        public Cicle(int x, int y, int row, int column, int index) {
            this.x = x;
            this.y = y;
            this.row = row;
            this.column = column;
            this.index = index;
        }

        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getRow() {
            return this.row;
        }

        public int getColumn() {
            return this.column;
        }

        public int getIndex() {
            return this.index;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
