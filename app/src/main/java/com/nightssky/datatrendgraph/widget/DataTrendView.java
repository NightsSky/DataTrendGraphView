package com.nightssky.datatrendgraph.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nightssky.datatrendgraph.Utils.DisplayUtils;
import com.nightssky.datatrendgraph.Utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/3/6.
 */

public class DataTrendView extends View {
    private float lineSmoothness = 0.2f;//调节曲线平滑度
    private Paint mPaint;
    private Paint textPaint;
    private Context mContext;

    private String[] dataCount = {"0","4万","8万","12万","16万"};
    private String[] date;
    private List<Integer> mockData;
    private Path mPath;
    private Path mAssistPath;
    private List<Point> mPointList;
    private PathMeasure mPathMeasure;
    private float drawScale = 1f;
    private int widthSize;
    private int heightSize;
    private boolean isClicked;
    private int clickY;
    private int clickX;

    public DataTrendView(Context context) {
        super(context);
    }

    public DataTrendView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPaint();
        initDate();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

        textPaint = new Paint();          // 创建画笔
        textPaint.setColor(Color.BLACK);        // 设置颜色
        textPaint.setStyle(Paint.Style.FILL);   // 设置样式

    }
    private void initDate() {
        date = TimeUtils.getWeekDate();
    }
    public void setData(List data){
        if (data!=null){
            this.mockData = data;
            mPointList = new ArrayList<>();
            int width = DisplayUtils.getScreenWidth((Activity) mContext);
            int childWidth = width/6;
            float childHeight = width*3/4 /2/16;
            for (int i = 0 ;i<mockData.size();i++) {
                mPointList.add(new Point(childWidth*i, (int) (-childHeight* mockData.get(i)/10000)));
            }
            measurePath();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
         widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //使宽度充满屏幕 ，高度为宽度的3/4 ；
        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = widthSize*3/4;
        }
        setMeasuredDimension(widthSize, heightSize*9/8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,heightSize);
        drawGrid(canvas);
        drawDate(canvas);
        drawBezier(canvas);
//        drawPoint(canvas);

        if (isClicked) {
            drawMoveInfo(canvas);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    clickX = (int) event.getX();
                    clickY = (int) event.getY();
                isClicked = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                clickX = (int) event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isClicked = false;
                invalidate();
                break;
        }
        return true;
    }
    private void drawMoveInfo(Canvas canvas) {

        int mhight = heightSize / 8 / 2 * 11;
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FCB377"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        canvas.drawLine(clickX,-mhight,clickX,0,paint);

        RectF rectF = new RectF(clickX-150,-(heightSize / 8 / 2 * 13),clickX+150,-mhight);
        canvas.drawRoundRect(rectF,30,30,paint);
        paint.setTextSize(48);
        paint.setColor(Color.WHITE);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        canvas.drawText("展现："+getAreaDate(),clickX-150,-(mhight+(heightSize/8-fontHeight)/2),paint);

    }

    /**
     * 获取对应数据
     * 不是很精确 找不到在path上找相交点坐标的方法
     *
     * 按比例模拟计算结果
     * @return
     */
    private int getAreaDate() {
        int childWidth = widthSize/6;
        for (int i = 1 ; i<=6;i++) {
            if (clickX<childWidth*i){

                int dafValue = mockData.get(i) - mockData.get(i - 1);
                int addValue = (clickX - childWidth * (i - 1)) *dafValue/ childWidth;
//                Log.d("msg", dafValue + "==" + addValue);
                return mockData.get(i-1)+addValue;
            }
        }
        return 0;
    }
    /**
     * 画曲线
     */
    private void drawBezier(Canvas canvas) {
        canvas.translate(0,0);
        Path dst = new Path();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#93C8F1"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        float distance = mPathMeasure.getLength() * drawScale;
        if (mPathMeasure.getSegment(0, distance, dst, true)) {
            //绘制曲线
            canvas.drawPath(dst, paint);

            float[] pos = new float[2];
            mPathMeasure.getPosTan(distance, pos, null);
            //绘制阴影
            drawShadowArea(canvas, dst);
        }
    }
    /**
     * 画细表格线
     * @param canvas
     */
    private void drawGrid(Canvas canvas) {
        int childHeight = heightSize / 8;
        for (int i = 0;i<5;i++){
            int curHeight = -childHeight * i;
            canvas.drawLine(0,curHeight,widthSize,curHeight,mPaint);
        }
    }
    /**
     * 画日期及纵坐标数据
     */
    private void drawDate(Canvas canvas) {
        textPaint.setTextSize(50);
        int childHeight = heightSize / 8;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0;i<5;i++){
            float curHeight =- childHeight * i-(childHeight-fontHeight)/2;
            canvas.drawText(dataCount[i], 5,curHeight,textPaint);
        }


        textPaint.setTextSize(40);
        int childWeight = widthSize/7;
        for (int i = 0;i<7;i++){
            canvas.drawText(date[i],childWeight*i,fontHeight,textPaint);
        }

        textPaint.setTextSize(60);
        float textWidth = textPaint.measureText(TimeUtils.getCurrentDate());//测量文字宽度
        canvas.drawText(TimeUtils.getCurrentDate(),(widthSize - textWidth)/2,
                -childHeight*7-(childHeight-fontHeight)/2,textPaint);
    }
    /**
     *画数据所在点
     */
    private void drawPoint(Canvas canvas) {
        if (mockData == null) {
            return ;
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        for (int i = 0 ;i<mockData.size();i++) {
            canvas.drawPoint(mPointList.get(i).x,mPointList.get(i).y,paint);
        }
    }
    /**
     * 绘制阴影
     * @param canvas
     * @param path
     */
    private void drawShadowArea(Canvas canvas, Path path) {
        path.lineTo(widthSize, 0);
        path.lineTo(0, 0);
        path.close();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xB3D7EBFA);
        canvas.drawPath(path, paint);
    }

    /**
     * 贝塞尔曲线计算公式
     */
    private void measurePath() {
        mPath = new Path();
        mAssistPath = new Path();
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        final int lineSize = mPointList.size();
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = mPointList.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = mPointList.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = mPointList.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                Point point = mPointList.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY);
                mAssistPath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (lineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (lineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (lineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (lineSmoothness * secondDiffY);
                //画出曲线
                mPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
                //将控制点保存到辅助路径上
                mAssistPath.lineTo(firstControlPointX, firstControlPointY);
                mAssistPath.lineTo(secondControlPointX, secondControlPointY);
                mAssistPath.lineTo(currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
        mPathMeasure = new PathMeasure(mPath, false);
    }
}
