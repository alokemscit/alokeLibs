package com.aloke.libraries.imagematch;

import android.graphics.Bitmap;
import android.graphics.Rect;


import com.aloke.libraries.imagematch.mobilefacenet.MobileFaceNet;
import com.aloke.libraries.imagematch.mtcnn.Align;
import com.aloke.libraries.imagematch.mtcnn.Box;
import com.aloke.libraries.imagematch.mtcnn.MTCNN;

import java.io.IOException;
import java.util.Vector;

public class ImageCheck {

    private Vector<Box> boxes1;
    private Vector<Box> boxes2;


    public static float IsSame(Bitmap bt1, Bitmap bt2, MTCNN mtcnn, MobileFaceNet mfn) throws IOException {
       // mtcnn = new MTCNN(assetManager);
       // mfn = new MobileFaceNet(assetManager);
        try {
            Bitmap bitmapTemp1 = bt1.copy(bt1.getConfig(), false);
            Bitmap bitmapTemp2 = bt2.copy(bt2.getConfig(), false);

            // 检测出人脸数据
            long start = System.currentTimeMillis();
            Vector<Box> boxes1 = mtcnn.detectFaces(bitmapTemp1, bitmapTemp1.getWidth() / 5); // 只有这句代码检测人脸，下面都是根据Box在图片中裁减出人脸
            //long end = System.currentTimeMillis();
            // resultTextView.setText("人脸检测前向传播耗时：" + (end - start));
            // resultTextView2.setText("");
            Vector<Box> boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5); // 只有这句代码检测人脸，下面都是根据Box在图片中裁减出人脸
            //if (boxes1.size() == 0 || boxes2.size() == 0) {
            //     Toast.makeText(MainActivity.this, "未检测到人脸", Toast.LENGTH_LONG).show();
            //    return;
            //  }

            // 这里因为使用的每张照片里只有一张人脸，所以取第一个值，用来剪裁人脸
            Box box1 = boxes1.get(0);
            Box box2 = boxes2.get(0);

            // 人脸矫正
            bitmapTemp1 = Align.face_align(bitmapTemp1, box1.landmark);
            bitmapTemp2 = Align.face_align(bitmapTemp2, box2.landmark);
            boxes1 = mtcnn.detectFaces(bitmapTemp1, bitmapTemp1.getWidth() / 5);
            boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5);
            box1 = boxes1.get(0);
            box2 = boxes2.get(0);

            box1.toSquareShape();
            box2.toSquareShape();
            box1.limitSquare(bitmapTemp1.getWidth(), bitmapTemp1.getHeight());
            box2.limitSquare(bitmapTemp2.getWidth(), bitmapTemp2.getHeight());
            Rect rect1 = box1.transform2Rect();
            Rect rect2 = box2.transform2Rect();
            // 剪裁人脸
            Bitmap bitmapCrop1 = MyUtil.crop(bitmapTemp1, rect1);
            Bitmap bitmapCrop2 = MyUtil.crop(bitmapTemp2, rect2);
            //var x=mfn.compare(bitmapCrop1, bitmapCrop2);
            return mfn.compare(bitmapCrop1, bitmapCrop2);
        }catch(Exception ex){
            return 0.00f;
        }
        //return  0F;



    }





}
