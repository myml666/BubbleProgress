package com.itfitness.bubbleprogress.widget;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.itfitness.bubbleprogress.R;

/**
 * @ProjectName: BubbleProgress
 * @Package: com.itfitness.bubbleprogress.widget
 * @ClassName: BubbleProgressView
 * @Description: java类作用描述 ：
 * @Author: 作者名：lml
 * @CreateDate: 2019/2/25 9:10
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/2/25 9:10
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */

public class BubbleProgressView extends View {
    private Paint mPaintProgress,mPaintBubble, mPaintProgressStr;
    private PathMeasure mPathMeasure;
    private Path mPathSrc,mPathDst,mPathBubble;
    private int mColorProgressBg;//进度条的背景颜色
    private int mColorProgress;//进度条的进度颜色
    private int mColorProgressStr = Color.WHITE;//进度条的进度文字的颜色
    private float mProgressHeight = 5;
    private float mProgress=0;//进度条的进度
    private float mBubbleTriangleHeight = 5;//气泡底部小三角高度
    private float mBubbleRectRound = 5;//气泡的圆角
    private String mProgressStr = "0%";//显示进度的字符串
    private float mTextSize = 20;//进度条文字大小
    private Paint.FontMetricsInt mFontMetricsInt;
    private float mProgressStrMargin = 15;//气泡的边距
    public BubbleProgressView(Context context) {
        this(context,null);
    }

    public BubbleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BubbleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(mProgressHeight);

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setStrokeCap(Paint.Cap.ROUND);//设置线头为圆角
        mPaintBubble.setStyle(Paint.Style.FILL);
        mPaintBubble.setStrokeJoin(Paint.Join.ROUND);//设置拐角为圆角

        mPaintProgressStr = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressStr.setStrokeWidth(1);
        mPaintProgressStr.setStyle(Paint.Style.STROKE);
        mPaintProgressStr.setColor(mColorProgressStr);
        mPaintProgressStr.setTextSize(mTextSize);//设置字体大小
        mPaintProgressStr.setTextAlign(Paint.Align.CENTER);//将文字水平居中

        mPathSrc = new Path();
        mPathDst = new Path();
        mPathBubble = new Path();
        mPathMeasure = new PathMeasure();

        mColorProgressBg = Color.GRAY;
        mColorProgress = getResources().getColor(R.color.colorAccent);
        mPaintBubble.setColor(getResources().getColor(R.color.colorAccent));//设置气泡的颜色

        mFontMetricsInt = mPaintProgressStr.getFontMetricsInt();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPathSrc.moveTo(30,h-mProgressHeight*2);
        mPathSrc.lineTo(w-30,h-mProgressHeight*2);//进度条位置在控件整体底部，且距离控件左边和右边各20像素
        mPathMeasure.setPath(mPathSrc,false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画进度条
        drawProgress(canvas);
        //画气泡
        drawBubble(canvas);
    }

    private void drawBubble(Canvas canvas) {
        Rect rect = new Rect();
        mPaintProgressStr.getTextBounds(mProgressStr,0,mProgressStr.length(),rect);//返回包围整个字符串的最小的一个Rect区域，以此计算出文字的高度和宽度
        int width = (int) (rect.width()+mProgressStrMargin);//计算字符串宽度(加上设置的边距)
        int height = (int) (rect.height()+mProgressStrMargin);//计算字符串高度(加上设置的边距)
        mPathBubble.reset();
        float p[] = new float[2];//用于存储点坐标的数组
        float t[] = new float[2];
        float stop = mPathMeasure.getLength() * mProgress;//计算进度条的进度
        mPathMeasure.getPosTan(stop,p,t);//获取进度所对应点的左边
        mPathBubble.moveTo(p[0],p[1]-mProgressHeight);
        mPathBubble.lineTo(p[0]+mBubbleTriangleHeight,p[1]-mBubbleTriangleHeight-mProgressHeight);//假设底部小三角为等腰直角三角形，那么三角形的高度就等于底边长度的1/2
        mPathBubble.lineTo(p[0]-mBubbleTriangleHeight,p[1]-mBubbleTriangleHeight-mProgressHeight);
        mPathBubble.close();//使路径闭合从而形成三角形
        //这里是计算文字所在矩形的位置及大小
        //left:因为设置的气泡底部三角形为等腰直角三角形，所以矩形的左边位置为，
        //      进度所在的横坐标 - 底部三角形高度 - 矩形圆角的半径(不减去圆角半径的话显得不够圆润)，
        //      而(mProgress*width)则是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置
        //      否则在进度条开始或结束位置可能为显示不全
        //top:进度所在的高度 - 底部三角形高度 - 进度条高度 - 矩形高度
        //right:矩形右边位置的计算原理与左边相同，同样((1-mProgress)*width)也是为了不断改变气泡底部的三角形与气泡顶部矩形的相对位置（与left相对应）
        //bottom:这个就简单了，与top相比小了一个矩形的高度
        RectF rectF = new RectF(p[0]-mBubbleTriangleHeight-mBubbleRectRound/2-(mProgress*width),p[1]-mBubbleTriangleHeight-mProgressHeight-height,p[0]+mBubbleTriangleHeight+mBubbleRectRound/2+((1-mProgress)*width),p[1]-mBubbleTriangleHeight-mProgressHeight);
        mPathBubble.addRoundRect(rectF,mBubbleRectRound,mBubbleRectRound, Path.Direction.CW);//添加矩形路径
        canvas.drawPath(mPathBubble,mPaintBubble);//绘制气泡
        int i = (mFontMetricsInt.bottom - mFontMetricsInt.ascent) / 2 - mFontMetricsInt.bottom;//让文字垂直居中
        canvas.drawText(mProgressStr,rectF.centerX(),rectF.centerY()+i,mPaintProgressStr);//绘制文字（将文字绘制在气泡矩形的中心点位置）
    }

    private void drawProgress(Canvas canvas) {
        mPathDst.reset();
        mPaintProgress.setColor(mColorProgressBg);
        canvas.drawPath(mPathSrc, mPaintProgress);//绘制进度背景（灰色部分）
        float stop = mPathMeasure.getLength() * mProgress;//计算进度条的进度
        mPathMeasure.getSegment(0,stop,mPathDst,true);//得到与进度对应的路径
        mPaintProgress.setColor(mColorProgress);
        canvas.drawPath(mPathDst, mPaintProgress);//绘制进度
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(float progress){
        mProgress = progress;
        mProgressStr = (int)(progress*100)+"%";
        invalidate();//设置完进度进行重绘
    }

    /**
     * 设置动画进度
     */
    public void setProgressWithAnim(float progress){
        ObjectAnimator.ofFloat(this,"progress",0,progress).setDuration(2000).start();
    }
}
