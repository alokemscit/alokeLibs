

package com.aloke.library

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aloke.library.databinding.ActivityFaceCheck1Binding
import android.widget.RelativeLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.io.ByteArrayOutputStream
import java.io.IOException

@ObsoleteCoroutinesApi
class ActivityFaceCheck : AppCompatActivity(){
    var arrchk:MutableList<String> = ArrayList()
    var arrchk2:MutableList<String> = ArrayList()

    private lateinit var binding: ActivityFaceCheck1Binding

    private var enginePrepared: Boolean = false
    private lateinit var engineWrapper: EngineWrapper
    private var threshold: Float = defaultThreshold

    private var camera: Camera? = null
    private var cameraId: Int = Camera.CameraInfo.CAMERA_FACING_FRONT
    private val previewWidth: Int = 640
    private val previewHeight: Int = 480
    private var isRunning:Boolean=false
    //private var mtcnn: MTCNN? = null
    //private var mfn: MobileFaceNet? = null
    /**
     *    1       2       3       4        5          6          7            8
     * <p>
     * 888888  888888      88  88      8888888888  88                  88  8888888888
     * 88          88      88  88      88  88      88  88          88  88      88  88
     * 8888      8888    8888  8888    88          8888888888  8888888888          88
     * 88          88      88  88
     * 88          88  888888  888888
     */
    private val frameOrientation: Int = 7

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    private var factorX: Float = 0F
    private var factorY: Float = 0F

    private val detectionContext = newSingleThreadContext("detection")
    private var working: Boolean = false

    private lateinit var scaleAnimator: ObjectAnimator
    private var bt1: Bitmap? =null
    private var bitmapTemp1: Bitmap? =null
    private var bitmapTemp2: Bitmap? =null

    private var bt2: Bitmap? =null
    private var t_bar:Int=0
    private var cnt:Int=0
    private var arrSize:Int=10
    private var key:String="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // queue = Volley.newRequestQueue(applicationContext);
        isRunning=false;
        arrchk2=ArrayList();






        // bt2 = ImageUtil.resize(Utils.convert(MyClass(applicationContext).GetTempDataValue("img_face2")), 200, 210).copy(Bitmap.Config.ARGB_8888, true)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        // or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)


        if (hasPermissions()) {
            init()
        } else {
            requestPermission()
        }


    }




    private fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermission() = requestPermissions(permissions, permissionReqCode)





    private fun init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_face_check1)
        val display: Display = windowManager.defaultDisplay
        val width: Int = display.getWidth() // deprecated

        val height: Int = display.getHeight() // d

        val layout = findViewById<View>(R.id.previewView) as RelativeLayout
        val params: ViewGroup.LayoutParams = layout.layoutParams
        params.height =  ( width*1.33333).toInt()
        params.width = width
        binding.result = DetectionResult()

        key= getIntent().getStringExtra("key").toString()
        Log.i("KeyValue",key)
        if(key.equals("x")){
            arrSize=4;
        }


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var widths = displayMetrics.widthPixels
        var heights = displayMetrics.heightPixels


        calculateSize()





        binding.surface.holder.let {
            it.setFormat(ImageFormat.NV21)
            it.addCallback(object : SurfaceHolder.Callback, Camera.PreviewCallback {
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    if (holder?.surface == null) return

                    if (camera == null) return

                    try {
                        camera?.stopPreview()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    val parameters = camera?.parameters
                    parameters?.setPreviewSize(previewWidth, previewHeight)
                    //  camera?.startSmoothZoom(3)

                    factorX = screenWidth / previewHeight.toFloat()
                    factorY = screenHeight / previewWidth.toFloat()




                    camera?.parameters = parameters
                    setCameraDisplayOrientation()
                    camera?.startPreview()
                    camera?.setPreviewCallback(this)


                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    camera?.setPreviewCallback(null)
                    camera?.release()
                    camera = null
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        camera = Camera.open(cameraId)
                    } catch (e: Exception) {
                        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
                        camera = Camera.open(cameraId)
                    }

                    try {
                        camera!!.setPreviewDisplay(binding.surface.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                    if (enginePrepared && data != null) {
                        if (!working) {
                            GlobalScope.launch(detectionContext) {
                                working = true
                                val result = engineWrapper.detect(
                                    data,
                                    previewWidth,
                                    previewHeight,
                                    frameOrientation
                                )
                                result.threshold = threshold

                                if(result.confidence>=0.998){
                                    //val animals: MutableList<String> = ArrayList()
                                    if(isRunning==false) {
                                        arrchk.add("1");
                                    }
                                    //arrayname = arrayOf<Int>()
                                    // arrayname.set(arrayname.size,0)
                                }
                                else{
                                    arrchk=ArrayList();
                                    // arrayname= arrayOf<Int>()
                                }


                                Log.i("Condition_1", isRunning.toString())
                                Log.i("Condition_2", arrchk.size.toString())
                                // Log.i("Condition_3", "Before")
                                if(arrchk.size>2 && isRunning==false && result.confidence>=0.998) {
                                    //  camera.pi
                                    Log.i("Condition1", "Yes")

                                    val parameters = camera!!.parameters
                                    val width = parameters.previewSize.width
                                    val height = parameters.previewSize.height

                                    val yuv = YuvImage(
                                        data,
                                        parameters.previewFormat,
                                        width,
                                        height,
                                        null
                                    )

                                    //   cnt+=1;
                                    // t_bar += 1;
                                    //arrchk= ArrayList()

                                    val out = ByteArrayOutputStream()
                                    yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
                                    val bytes: ByteArray = out.toByteArray()
                                    val bitmaps = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                    val degrees = 270f //rotation degree

                                    val matrix = Matrix()
                                    matrix.setRotate(degrees)
                                    var bitmap =Bitmap.createBitmap(bitmaps, 0, 0, bitmaps.getWidth(), bitmaps.getHeight(), matrix, true)


                                    t_bar= if(t_bar>1)  0 else t_bar

                                    // bitmaps = bitmaps.rotate(90f)


                                }
                                //}


                                val rect = calculateBoxLocationOnScreen(
                                    result.left,
                                    result.top,
                                    result.right,
                                    result.bottom
                                )

                                binding.result = result.updateLocation(rect)

                                /* Log.d(
                                     tag,
                                     "threshold:${result.threshold}, confidence: ${result.confidence}"
                                 )*/

                                binding.rectView.postInvalidate()
                                working = false
                            }
                        }
                    }
                }
            })
        }

        scaleAnimator = ObjectAnimator.ofFloat(binding.scan, View.SCALE_Y, 1F, -1F, 1F).apply {
            this.duration = 3000
            this.repeatCount = ValueAnimator.INFINITE
            this.repeatMode = ValueAnimator.REVERSE
            this.interpolator = LinearInterpolator()
            this.start()
        }



    }



    private fun calculateSize() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
    }

    private fun calculateBoxLocationOnScreen(left: Int, top: Int, right: Int, bottom: Int): Rect =
        Rect(
            (left * factorX).toInt(),
            (top * factorY).toInt(),
            (right * factorX).toInt(),
            (bottom * factorY).toInt()
        )

    private fun setCameraDisplayOrientation() {

        val matrix = Matrix()

        // 1
        //val centerX = previewView.width / 2f
        // val centerY = previewView.height / 2f

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        //  matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // 3
        //   previewView.setTransform(matrix)

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera!!.setDisplayOrientation(result)

    }

    /*fun setting(@Suppress("UNUSED_PARAMETER") view: View) =
        SetThresholdDialogFragment().show(supportFragmentManager, "dialog")*/

    /*override fun onDialogPositiveClick(t: Float) {
        threshold = t
    }*/

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionReqCode) {
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init()
            } else {
                //Toast.makeText(this, "Camera Permission required", Toast.LENGTH_LONG).show()
                requestPermission()

            }
        }
    }




    override fun onResume() {
        engineWrapper = EngineWrapper(assets)
        enginePrepared = engineWrapper.init()

        if (!enginePrepared) {
            Toast.makeText(this, "Engine init failed.", Toast.LENGTH_LONG).show()
        }

        super.onResume()

    }


    override fun onStart() {
        super.onStart()
        /* window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                 View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY*/

        window.decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )

    }
    override fun onDestroy() {
        isRunning=true;
        // working=false;
        engineWrapper.destroy()
        scaleAnimator.cancel()

        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null

        super.onDestroy()
    }

    companion object {
        const val tag = "MainActivity"
        const val defaultThreshold = 0.915F

        val permissions: Array<String> = arrayOf(Manifest.permission.CAMERA)
        const val permissionReqCode = 1
    }

}

