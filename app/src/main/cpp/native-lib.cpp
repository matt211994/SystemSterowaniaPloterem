#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/core/cvstd.inl.hpp>
#include <opencv2/imgcodecs/imgcodecs.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;


int toGray(Mat *img, Mat *gray){

    cvCvtColor(img,gray,CV_RGB2GRAY);
}

JNIEXPORT jint JNICALL Java_com_example_matjeusz_opencv_OpencvNativeclass_convertGray
        (JNIEnv *, jclass, jlong addrRgba, jlong addrGray){

    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;


    int conv;
    jint retVal;
    conv = toGray(&mRgb,&mGray);
    retVal =(jint)conv;

    return retVal;
}

