#include "com_example_matjeusz_opencv_OpenCVNativClass.h"
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/imgproc.hpp>
#include <cstdio>




vector<Point2i> mCorners;

int cx1,cy1,cx2,cy2;
Vec3b color;
int R,G,B;

JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_OpenCVNativClass_convertGray
  (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){

    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;
    conv = toGray(mRgb,mGray);
    retVal =(jint)conv;
    return retVal;
}





int toGray(Mat mRgb, Mat& gray){
      cvtColor(mRgb,gray,CV_RGB2GRAY);

        cv::goodFeaturesToTrack(gray,mCorners,2,0.01,1000,noArray(),3,false,0.04);
    cx1=mCorners[0].x;
    cy1=mCorners[0].y;
    cx2=mCorners[1].x;
    cy2=mCorners[1].y;
    //cv::rectangle(mRgb,Point(mCorners[0].x,mCorners[0].y),Point(mCorners[1].x,mCorners[1].y),Scalar(0,255,0),3,LINE_8,0);
    return 1;
}

JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_MainActivity_add
        (JNIEnv *, jclass){
    return cx1;
}

JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_MainActivity_add1
        (JNIEnv *, jclass){
    return cy1;
}

JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_MainActivity_add2
        (JNIEnv *, jclass){
    return cx2;
}
JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_MainActivity_add3
        (JNIEnv *, jclass){
    return cy2;
}