package com.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.faceunity.entity.CartoonFilter;
import com.faceunity.entity.Effect;
import com.faceunity.entity.FaceMakeup;
import com.faceunity.entity.Filter;
import com.faceunity.entity.LivePhoto;
import com.faceunity.entity.MakeupItem;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.param.BeautificationParam;
import com.faceunity.param.MakeupParamHelper;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.faceunity.wrapper.faceunity.FU_ADM_FLAG_FLIP_X;
import static com.faceunity.wrapper.faceunity.fuItemSetParam;


public class FURenderer implements OnFUControlListener {
    private static final String TAG = FURenderer.class.getSimpleName();
    public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;
    private Context mContext;

    private static final String BUNDLE_V3 = "v3.bundle";
    private static final String BUNDLE_FXAA = "fxaa.bundle";
    private static final String BUNDLE_FACE_BEAUTIFICATION = "beautify_face/face_beautification.bundle";
    private static final String BUNDLE_HAIR_MODEL_HIGH_SPEED = "beautify_hair/hair_model_w0305.bundle";
    private static final String BUNDLE_HAIR_NORMAL = "beautify_hair/hair_normal_no_face_no_render.bundle";
    private static final String BUNDLE_HAIR_GRADIENT = "beautify_hair/hair_gradient_no_face_no_render.bundle";
    private static final String BUNDLE_TONGUE = "tongue.bundle";

    private static final String BUNDLE_CHANGE_FACE = "change_face/change_face.bundle";

    private static final String BUNDLE_CARTOON_FILTER = "cartoon_filter/fuzzytoonfilter.bundle";

    private static final String BUNDLE_LIGHT_MAKEUP = "light_makeup/light_makeup.bundle";
    private static final String BUNDLE_FACE_MAKEUP = "makeup/face_makeup.bundle";
    private static final String BUNDLE_LIVE_PHOTO = "live_photo/photolive.bundle";
    private static final String BUNDLE_NEW_FACE_TRACKER = "makeup/new_face_tracker_normal.bundle";
    private static final String BUNDLE_AVATAR_BACKGROUND = "avatar/avatar_background.bundle";

    private static final String BUNDLE_BEAUTIFY_BODY = "beautify_body/BodySlim.bundle";

    private volatile static float mFilterLevel = 1.0f;
    private volatile static float mSkinDetect = 1.0f;
    private volatile static float mBlurLevel = 0.7f;
    private volatile static float mBlurType = 0.0f;
    private volatile static float mColorLevel = 0.3f;
    private volatile static float mRedLevel = 0.3f;
    private volatile static float mEyeBright = 0.0f;
    private volatile static float mToothWhiten = 0.0f;
    private volatile static float mFaceShape = BeautificationParam.FACE_SHAPE_CUSTOM;
    private volatile static float mFaceShapeLevel = 1.0f;
    private volatile static float mCheekThinning = 0f;
    private volatile static float mCheekV = 0.5f;
    private volatile static float mCheekNarrow = 0f;
    private volatile static float mCheekSmall = 0f;
    private volatile static float mEyeEnlarging = 0.4f;
    private volatile static float mIntensityChin = 0.3f;
    private volatile static float mIntensityForehead = 0.3f;
    private volatile static float mIntensityMouth = 0.4f;
    private volatile static float mIntensityNose = 0.5f;

    private int mFrameId = 0;

    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;
    public static final int ITEM_ARRAYS_EFFECT_INDEX = 1;
    private static final int ITEM_ARRAYS_LIGHT_MAKEUP_INDEX = 2;
    private static final int ITEM_ARRAYS_ABIMOJI_3D_INDEX = 3;
    private static final int ITEM_ARRAYS_HAIR_NORMAL_INDEX = 4;
    private static final int ITEM_ARRAYS_HAIR_GRADIENT_INDEX = 5;
    private static final int ITEM_ARRAYS_CHANGE_FACE_INDEX = 6;
    private static final int ITEM_ARRAYS_CARTOON_FILTER_INDEX = 7;
    private static final int ITEM_ARRAYS_LIVE_PHOTO_INDEX = 8;
    private static final int ITEM_ARRAYS_FACE_MAKEUP_INDEX = 9;
    public static final int ITEM_ARRAYS_AVATAR_BACKGROUND = 10;
    public static final int ITEM_ARRAYS_AVATAR_HAIR = 11;
    private static final int ITEM_ARRAYS_NEW_FACE_TRACKER = 12;
    private static final int ITEM_ARRAYS_BEAUTIFY_BODY = 13;
    private static final int ITEM_ARRAYS_HAIR_MODEL = 14;

    private static final int ITEM_ARRAYS_COUNT = 15;

    //  track face 50
    private static final int MAX_TRACK_COUNT = 50;

    public static final int HAIR_NORMAL = 1;
    public static final int HAIR_GRADIENT = 2;

    private volatile static String sFilterName = Filter.Key.FENNEN_1;
    //handle
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    private Handler mFuItemHandler;

    private boolean isNeedBeautyHair = false;
    private boolean isNeedFaceBeauty = true;
    private boolean isNeedAnimoji3D = false;
    private boolean isNeedPosterFace = false;
    private volatile Effect mDefaultEffect;
    private boolean mIsCreateEGLContext; //EGLContext
    private int mInputTextureType = 0; //texture???Camera EXTERNAL OES
    private int mInputImageFormat = 0;
    private volatile boolean isNeedUpdateFaceBeauty = true;

    private boolean mUseBeautifyBody;
    private float mBodySlimStrength = 0.0f;
    private float mLegSlimStrength = 0.0f;
    private float mWaistSlimStrength = 0.0f;
    private float mShoulderSlimStrength = 0.5f;
    private float mHipSlimStrength = 0.0f;

    private volatile int mInputImageOrientation = 270;
    private volatile int mInputPropOrientation = 270;
    private volatile int mIsInputImage = 0;
    private volatile int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private volatile int mMaxFaces = 4;

    private volatile float mHairColorStrength = 0.6f;
    private volatile int mHairColorType = HAIR_GRADIENT;
    private volatile int mHairColorIndex = 0;


    private Map<String, Object> mMakeupParams = new ConcurrentHashMap<>(16);

    private Map<Integer, MakeupItem> mLightMakeupItemMap = new ConcurrentHashMap<>(16);
    private double[] mLipStickColor;

    private float[] landmarksData = new float[150];
    private float[] expressionData = new float[46];
    private float[] rotationData = new float[4];
    private float[] pupilPosData = new float[2];
    private float[] rotationModeData = new float[1];
    private float[] faceRectData = new float[4];
    private double[] posterTemplateLandmark;
    private double[] posterPhotoLandmark;

    private List<Runnable> mEventQueue;
    private OnBundleLoadCompleteListener mOnBundleLoadCompleteListener;
    private volatile int mComicFilterStyle = CartoonFilter.NO_FILTER;
    private static boolean mIsInited;
    private volatile int mDefaultOrientation = 90;
    private int mRotMode = 1;
    private boolean mNeedBackground;

    /**
     * SDK???
     */
    public static void initFURenderer(Context context) {
        if (mIsInited) {
            return;
        }
        try {
            //faceunity SDK
           // Log.e(TAG, "fu sdk version " + faceunity.fuGetVersion());
            long startTime = System.currentTimeMillis();
            InputStream v3 = context.getAssets().open(BUNDLE_V3);
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            faceunity.fuSetup(v3Data, authpack.A());

            /**
             * fuLoadTongueModel
             * tongue.bundle???
             */
            InputStream tongue = context.getAssets().open(BUNDLE_TONGUE);
            byte[] tongueDate = new byte[tongue.available()];
            tongue.read(tongueDate);
            tongue.close();
            faceunity.fuLoadTongueModel(tongueDate);

            long duration = System.currentTimeMillis() - startTime;
            Log.i(TAG, "setup fu sdk finish: " + duration + "ms");
            mIsInited = true;
        } catch (Exception e) {
            Log.e(TAG, "initFURenderer error", e);
        }
    }

    /**
     * AgoraLive implementation
     *
     * @param image      virtual image effect
     * @param background effect used to draw background images
     */
    public void onEffectImageSelected(Effect image, final Effect background) {
        onEffectSelected(image);
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                final int backgroundItem = loadItem(background.path());
                if (backgroundItem <= 0) {
                    Log.w(TAG, "load background item failed: " + backgroundItem);
                }

                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = backgroundItem;
                    }
                });
            }
        });
    }

    public void onEffectImageSelected(Effect imageEffect, int imageHandle, int backgroundHandle, boolean updateBeauty) {
        mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = backgroundHandle;

        if (imageHandle > 0) {
            updateEffectItemParams(imageEffect, imageHandle);
            // Private parameter to set the virtual image in the middle
            fuItemSetParam(imageHandle, "{\"thing\":\"<global>\",\"param\":\"follow\"}", 0);
        }

        mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = imageHandle;
        if (mOnBundleLoadCompleteListener != null) {
            mOnBundleLoadCompleteListener.onBundleLoadComplete(ITEM_ARRAYS_EFFECT_INDEX);
        }
    }

    /**
     *
     * @param style
     */
    @Override
    public void onCartoonFilterSelected(final int style) {
        if (mComicFilterStyle == style) {
            return;
        }
        mComicFilterStyle = style;
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_CARTOON_FILTER_INDEX, mComicFilterStyle));
                }
            });
        } else {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_CARTOON_FILTER_INDEX, mComicFilterStyle));
        }
    }

    /**
     * faceunity sdk
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    public static int getModuleCode(int index) {
        return faceunity.fuGetModuleCode(index);
    }

    /**
     * FURenderer
     */
    private FURenderer(Context context, boolean isCreateEGLContext) {
        this.mContext = context;
        this.mIsCreateEGLContext = isCreateEGLContext;
    }

    /**
     * faceunity
     */
    public void onSurfaceCreated() {
        Log.e(TAG, "onSurfaceCreated");
        onSurfaceDestroyed();

        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>(16));

        HandlerThread handlerThread = new HandlerThread("FUItemWorker");
        handlerThread.start();
        mFuItemHandler = new FUItemHandler(handlerThread.getLooper());

        /**
         * fuCreateEGLContext OpenGL
         * OpenGL
         * fuCreateEGLContext???fuReleaseEGLContext
         */
        if (mIsCreateEGLContext) {
            faceunity.fuCreateEGLContext();
        }

        mFrameId = 0;
        /**
         *fuSetExpressionCalibration ???0???2???
         * SDK???fuSetExpressionCalibration0,2???1???
         */
        faceunity.fuSetExpressionCalibration(2);
        faceunity.fuSetMaxFaces(mMaxFaces);//?????????????????????????????????8??????

        if (isNeedFaceBeauty) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_FACE_BEAUTY_INDEX);
        }
        if (isNeedBeautyHair) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_HAIR_MODEL);
            if (mHairColorType == HAIR_NORMAL) {
                mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_HAIR_NORMAL_INDEX);
            } else {
                mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_HAIR_GRADIENT_INDEX);
            }
        }
        if (isNeedAnimoji3D) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_ABIMOJI_3D_INDEX);
        }
        if (isNeedPosterFace) {
            posterPhotoLandmark = new double[150];
            posterTemplateLandmark = new double[150];
            mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX] = loadItem(BUNDLE_CHANGE_FACE);
        }


        int style = mComicFilterStyle;
        mComicFilterStyle = CartoonFilter.NO_FILTER;
        onCartoonFilterSelected(style);

        if (mNeedBackground) {
            loadAvatarBackground();
        }

        // ??????????????????????????????????????? animoji 3D ?????????????????????
        if (mDefaultEffect != null) {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
        }

        // ????????????????????????
        if (mMakeupParams.size() > 0) {
            selectMakeupItem(new HashMap<>(mMakeupParams), false);
        }

        // ??????????????????????????????
        if (mLightMakeupItemMap.size() > 0) {
            Set<Map.Entry<Integer, MakeupItem>> entries = mLightMakeupItemMap.entrySet();
            for (Map.Entry<Integer, MakeupItem> entry : entries) {
                MakeupItem makeupItem = entry.getValue();
                onLightMakeupSelected(makeupItem, makeupItem.getLevel());
            }
        }

        // ??????????????????????????????????????????
        if (mUseBeautifyBody) {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_BEAUTIFY_BODY));
        }

        // ????????????
        setAsyncTrackFace(false);
        // ?????? Animoji ???????????????
        setMouthExpression(0.5f);
    }

    /**
     * ?????? bundle ?????????????????? EGL Context?????????????????????
     *
     * @param bundlePath bundle ????????????
     * @return ????????????????????? 0 ??????????????????
     */
    private static int loadItem(Context context, String bundlePath) {
        int handle = 0;
        if (!TextUtils.isEmpty(bundlePath)) {
            byte[] buffer = FileUtils.readFile(context, bundlePath);
            if (buffer != null) {
                handle = faceunity.fuCreateItemFromPackage(buffer);
            }
        }
        Log.d(TAG, "loadItem. bundlePath: " + bundlePath + ", itemHandle: " + handle);
        return handle;
    }

    /**
     * ???????????????(fuRenderToNV21Image)
     *
     * @param img NV21??????
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * ???????????????(fuRenderToNV21Image)??????????????????????????????????????????byte[]
     *
     * @param img         NV21??????
     * @param w
     * @param h
     * @param readBackImg ??????????????????????????????byte[]
     * @param readBackW
     * @param readBackH
     * @return
     */
    public int onDrawFrame(byte[] img, int w, int h, byte[] readBackImg, int readBackW, int readBackH) {
        if (img == null || w <= 0 || h <= 0 || readBackImg == null || readBackW <= 0 || readBackH <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * ???????????????(fuDualInputToTexture)(????????????????????????????????????????????????)????????????????????????????????????????????????????????????????????????
     *
     * @param img NV21??????
     * @param tex ??????ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * ???????????????(fuDualInputToTexture)??????????????????????????????????????????byte[]
     *
     * @param img         NV21??????
     * @param tex         ??????ID
     * @param w
     * @param h
     * @param readBackImg ??????????????????????????????byte[]
     * @param readBackW
     * @param readBackH
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h, byte[] readBackImg, int readBackW, int readBackH) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0 || readBackImg == null || readBackW <= 0 || readBackH <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * ???????????????(fuRenderToTexture)
     *
     * @param tex ??????ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(int tex, int w, int h) {
        if (tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToTexture(tex, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * ???????????????(fuBeautifyImage)????????????????????????????????????SDK?????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????? fuDualInputToTexture ??????????????????????????????????????????????????????????????????????????????
     *
     * @param tex ??????ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameBeautify(int tex, int w, int h) {
        if (tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuBeautifyImage(tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
    }

    /**
     * ??????faceunity???????????????
     */
    public void onSurfaceDestroyed() {
        Log.e(TAG, "onSurfaceDestroyed");
        if (mFuItemHandler != null) {
            mFuItemHandler.getLooper().quitSafely();
            mFuItemHandler = null;
        }
        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }

        int posterIndex = mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX];
        if (posterIndex > 0) {
            faceunity.fuDeleteTexForItem(posterIndex, "tex_input");
            faceunity.fuDeleteTexForItem(posterIndex, "tex_template");
        }

        int lightMakeupIndex = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
        if (lightMakeupIndex > 0) {
            Set<Integer> makeupTypes = mLightMakeupItemMap.keySet();
            for (Integer makeupType : makeupTypes) {
                faceunity.fuDeleteTexForItem(lightMakeupIndex, MakeupParamHelper.getMakeupTextureKeyByType(makeupType));
            }
        }

        int faceMakeupIndex = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
        if (faceMakeupIndex > 0) {
            Set<String> keys = mMakeupParams.keySet();
            for (String key : keys) {
                if (key.startsWith(MakeupParamHelper.MakeupParam.TEX_PREFIX)) {
                    faceunity.fuDeleteTexForItem(faceMakeupIndex, key);
                }
            }
        }
        int livePhotoPhotoIndex = mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX];
        if (livePhotoPhotoIndex > 0) {
            faceunity.fuDeleteTexForItem(livePhotoPhotoIndex, "tex_input");
        }

        mFrameId = 0;
        isNeedUpdateFaceBeauty = true;
        Arrays.fill(mItemsArray, 0);
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        faceunity.fuDone();
        if (mIsCreateEGLContext) {
            faceunity.fuReleaseEGLContext();
        }
    }

    /**
     * ?????? landmark ??????
     *
     * @param faceId
     * @return ?????????????????????????????????????????????????????? Nama ????????????????????????
     */
    public float[] getLandmarksData(int faceId) {
        int isTracking = faceunity.fuIsTracking();
        Arrays.fill(landmarksData, 0.0f);
        if (isTracking > 0) {
            faceunity.fuGetFaceInfo(faceId, "landmarks", landmarksData);
        }
        return landmarksData;
    }

    public int trackFace(byte[] img, int w, int h) {
        if (img == null) {
            return 0;
        }
        faceunity.fuOnCameraChange();
        int flags = mInputImageFormat;
        for (int i = 0; i < MAX_TRACK_COUNT; i++) {
            faceunity.fuTrackFace(img, flags, w, h);
        }
        return faceunity.fuIsTracking();
    }

    public float[] getFaceRectData(int i) {
        Arrays.fill(faceRectData, 0.0f);
        faceunity.fuGetFaceInfo(i, "face_rect", faceRectData);
        return faceRectData;
    }

    //--------------------------------------????????????????????????----------------------------------------

    /**
     * ?????? fuTrackFace + fuAvatarToTexture ?????????????????????????????????????????????camera????????????????????????animoji???????????????????????????
     * fuTrackFace ??????????????????????????????
     * fuAvatarToTexture ??????????????????????????????
     *
     * @param img ?????????????????? flags ??????
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameAvatar(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrameAvatar data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mNeedBenchmark) {
            mFuCallStartTime = System.nanoTime();
        }

        faceunity.fuTrackFace(img, flags, w, h);
        int isTracking = faceunity.fuIsTracking();

        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(rotationData, 0.0f);
        Arrays.fill(expressionData, 0.0f);
        Arrays.fill(pupilPosData, 0.0f);
        Arrays.fill(rotationModeData, 0.0f);

        if (isTracking > 0) {
            /**
             * landmarks 2D??????????????????????????????75????????????????????????75*2
             */
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            /**
             *rotation ?????????????????????????????????????????????????????????4
             */
            faceunity.fuGetFaceInfo(0, "rotation", rotationData);
            /**
             * expression  ?????????????????????46
             */
            faceunity.fuGetFaceInfo(0, "expression", expressionData);
            /**
             * pupil pos ?????????????????????2
             */
            faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);
            /**
             * rotation mode ???????????????0-3???????????????????????????????????????1
             */
            faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);
        } else {
            rotationData[3] = 1.0f;
            rotationModeData[0] = 1.0f * (360 - mInputImageOrientation) / 90;
        }

        int tex = faceunity.fuAvatarToTexture(AvatarConstant.PUP_POS_DATA, AvatarConstant.EXPRESSIONS,
                AvatarConstant.ROTATION_DATA, rotationModeData, 0, w, h, mFrameId++, mItemsArray,
                AvatarConstant.VALID_DATA);
        if (mNeedBenchmark) {
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        }
        return tex;
    }

    /**
     * ??????????????????
     */
    public void enterFaceShape() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "enter_facepup", 1);
                }
            }
        });
    }

    /**
     * ????????????????????????
     */
    public void clearFaceShape() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "clear_facepup", 1);
                }

                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], "clear_facepup", 1);
                }
            }
        });
    }

    /**
     * ?????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public void quitFaceup() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "quit_facepup", 1);
                }
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public void recomputeFaceup() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "need_recompute_facepup", 1);
                }
            }
        });
    }

    /**
     * ????????????????????????????????????[0-1]?????????param??????????????????????????????????????????1?????????
     *
     * @param key
     * @param value
     */
    public void fuItemSetParamFaceup(final String key, final double value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", value);
                }
            }
        });
    }

    /**
     * ?????? avatar ????????????
     *
     * @param key
     * @param value [r,g,b] ??? [r,g,b,intensity]
     */
    public void fuItemSetParamFaceColor(final String key, final double[] value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (value.length > 3) {
                    if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], key, value);
                    }
                } else {
                    if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], key, value);
                    }
                }
            }
        });
    }

    /**
     * whether avatar bundle is loaded
     *
     * @return
     */
    public boolean isAvatarLoaded() {
        return mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0;
    }

    /**
     * whether avatar hair and background bundle is loaded
     *
     * @return
     */
    public boolean isAvatarMakeupItemLoaded() {
        return mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0 && mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0;
    }

    /**
     * ????????????????????????,????????????????????????
     *
     * @param scale
     */
    public void setAvatarScale(final float scale) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "absoluteScale", scale);
                }
            }
        });
    }

    /**
     * ????????????????????????,????????????????????????
     *
     * @param scale
     */
    public void setAvatarHairScale(final float scale) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], "absoluteScale", scale);
                }
            }
        });
    }

    /**
     * ????????????[x,y,z]?????????,????????????????????????
     *
     * @param xyz
     */
    public void setAvatarTranslate(final double[] xyz) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "absoluteTranslate", xyz);
                }
            }
        });
    }

    /**
     * ????????????[x,y,z]?????????,????????????????????????
     *
     * @param xyz
     */
    public void setAvatarHairTranslate(final double[] xyz) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], "absoluteTranslate", xyz);
                }
            }
        });
    }

    /**
     * ??????GLSurfaceView???queueEvent??????
     */
    public void queueEvent(Runnable r) {
        if (mEventQueue == null) {
            return;
        }
        mEventQueue.add(r);
    }

    /**
     * ????????????????????????
     *
     * @param isAsync ????????????
     */
    public void setAsyncTrackFace(final boolean isAsync) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuSetAsyncTrackFace(isAsync ? 1 : 0);
            }
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param maxFaces
     */
    public void setMaxFaces(final int maxFaces) {
        if (mMaxFaces != maxFaces && maxFaces > 0) {
            mMaxFaces = maxFaces;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuSetMaxFaces(mMaxFaces);
                }
            });
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param isFront ?????????????????????
     */
    public void setIsFrontCamera(final boolean isFront) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] > 0) {
                    if (isFront && mInputImageOrientation == 90) {
                        // ?????? Nexus ????????????????????????X???????????????
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_swap_x", 1.0);
                    } else {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_swap_x", 0.0);
                    }
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_front", isFront ? 1.0 : 0.0);
                }
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void prepareDrawFrame() {
        //??????FPS?????????
        benchmarkFPS();

        if (mUseBeautifyBody) {
            //  ?????????????????????
            int hasHuman = (int) faceunity.fuItemGetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], "HasHuman");
            if (mOnTrackingStatusChangedListener != null && mTrackingStatus != hasHuman) {
                mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = hasHuman);
            }
        } else {
            //????????????????????????????????????????????????
            int isTracking = faceunity.fuIsTracking();
            if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
                mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
            }
        }

        //??????faceunity????????????????????????????????????
        int error = faceunity.fuGetSystemError();
        if (error != 0) {
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
            if (mOnSystemErrorListener != null) {
                mOnSystemErrorListener.onSystemError(faceunity.fuGetSystemErrorString(error));
            }
        }

        //??????????????????
        if (isNeedUpdateFaceBeauty && mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
            int itemFaceBeauty = mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX];
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FILTER_NAME, sFilterName);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FILTER_LEVEL, mFilterLevel);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.SKIN_DETECT, mSkinDetect);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.HEAVY_BLUR, 0.0f);
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParam.BLUR_TYPE, mBlurType);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.BLUR_LEVEL, 6.0 * mBlurLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.COLOR_LEVEL, mColorLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.RED_LEVEL, mRedLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.EYE_BRIGHT, mEyeBright);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.TOOTH_WHITEN, mToothWhiten);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FACE_SHAPE_LEVEL, mFaceShapeLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FACE_SHAPE, mFaceShape);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.EYE_ENLARGING, mEyeEnlarging);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_THINNING, mCheekThinning);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_NARROW, mCheekNarrow);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_SMALL, mCheekSmall);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_V, mCheekV);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_NOSE, mIntensityNose);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_CHIN, mIntensityChin);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_FOREHEAD, mIntensityForehead);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_MOUTH, mIntensityMouth);
            isNeedUpdateFaceBeauty = false;
        }

        //queueEvent???Runnable??????????????????
        while (mEventQueue != null && !mEventQueue.isEmpty()) {
            mEventQueue.remove(0).run();
        }
    }

    /**
     * camera?????????????????????
     *
     * @param currentCameraType     ??????????????????ID
     * @param inputImageOrientation
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        if (mCurrentCameraType == currentCameraType && mInputImageOrientation == inputImageOrientation)
            return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                mInputPropOrientation = inputImageOrientation;
                faceunity.fuOnCameraChange();
                mRotMode = calculateRotMode();
                updateEffectItemParams(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                int trackerOrientation = calculateFaceTrackerOrientation();
                setFaceTrackerOrientation(trackerOrientation);
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    int orientation;
                    if (mIsInputImage == 1) {
                        orientation = 0;
                    } else {
                        orientation = trackerOrientation;
                    }
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.ORIENTATION, orientation);
                }
            }
        });
    }

    /**
     * camera?????????????????????
     *
     * @param currentCameraType     ??????????????????ID
     * @param inputImageOrientation
     * @param inputPropOrientation
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation
            , final int inputPropOrientation) {
        if (mCurrentCameraType == currentCameraType && mInputImageOrientation == inputImageOrientation &&
                mInputPropOrientation == inputPropOrientation)
            return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                mInputPropOrientation = inputPropOrientation;
                faceunity.fuOnCameraChange();
                mRotMode = calculateRotMode();
                updateEffectItemParams(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                setFaceTrackerOrientation(calculateFaceTrackerOrientation());
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param rotation
     */
    public void setTrackOrientation(final int rotation) {
        if (mDefaultOrientation != rotation) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mDefaultOrientation = rotation;
                    /* ?????????????????????????????????????????? 0-3????????????????????????????????????????????????0??????90??????180??????270??????
                     * Android ????????????????????????????????? 1???????????????????????????????????? 3??????????????????????????? */
                    faceunity.fuSetDefaultOrientation(mDefaultOrientation / 90);
                    mRotMode = calculateRotMode();
                    // ???????????? Animoji ???????????? ???????????? ???????????????????????????????????????????????????
                    if (mDefaultEffect != null && (mDefaultEffect.effectType() == Effect.EFFECT_TYPE_BACKGROUND
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_ANIMOJI
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_EXPRESSION
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_GESTURE
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_AVATAR)) {
                        faceunity.fuOnCameraChange();
                    }
                    int trackerOrientation = calculateFaceTrackerOrientation();
                    if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "rotMode", mRotMode);
                        // ???????????????????????????????????????????????????????????????????????????????????????????????????"rotationMode"
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "rotationMode", trackerOrientation);
                    }
                    Log.i(TAG, "setTrackOrientation. rotation: " + rotation + ", trackOrientation" + trackerOrientation);
                    setFaceTrackerOrientation(trackerOrientation);
                    if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                        int orientation;
                        if (mIsInputImage == 1) {
                            orientation = 0;
                        } else {
                            orientation = trackerOrientation;
                        }
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.ORIENTATION, orientation);
                    }
                }
            });
        }
    }

    /**
     * ?????? 209 ???????????????????????????
     *
     * @param orientation ??????????????????0 1 2 3 ???????????????????????????
     *                    <p>
     *                    FUAI_CAMERA_VIEW_ROT_0 = 0,
     *                    FUAI_CAMERA_VIEW_ROT_90 = 1,
     *                    FUAI_CAMERA_VIEW_ROT_180 = 2,
     *                    FUAI_CAMERA_VIEW_ROT_270 = 3,
     */
    public void setFaceTrackerOrientation(final int orientation) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_NEW_FACE_TRACKER] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_NEW_FACE_TRACKER], "orientation", orientation);
                }
            }
        });
    }

    /**
     * ???????????????????????????0????????????1?????????
     *
     * @param isFlipPoints
     */
    public void setIsFlipPoints(final boolean isFlipPoints) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX], "is_flip_points", isFlipPoints ? 1 : 0);
                }
            }
        });
    }

    /**
     * "mouth_expression_more_flexible"
     * 0 ??? 1 ?????????????????????????????? 0???????????????????????????????????????????????????????????????
     *
     * @param value
     */
    public void setMouthExpression(final float value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuSetFaceTrackParam("mouth_expression_more_flexible", value);
            }
        });
    }

    /**
     * ?????? RotMode
     *
     * @return
     */
    private int calculateRotMode() {
        int mode;
        if (mInputImageOrientation == 270) {
            if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mode = mDefaultOrientation / 90;
            } else {
                mode = (mDefaultOrientation - 180) / 90;
            }
        } else {
            if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mode = (mDefaultOrientation + 180) / 90;
            } else {
                mode = (mDefaultOrientation) / 90;
            }
        }
        return mode;
    }

    /**
     * ?????? 209 ???????????????????????????
     *
     * @return orientation
     */
    private int calculateFaceTrackerOrientation() {
        int orientation;
        if (mInputImageOrientation == 270) {
            if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                orientation = mDefaultOrientation / 90;
            } else {
                if (mDefaultOrientation == 90) {
                    orientation = 3;
                } else if (mDefaultOrientation == 270) {
                    orientation = 1;
                } else {
                    orientation = mDefaultOrientation / 90;
                }
            }
        } else {
            if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (mDefaultOrientation == 0) {
                    orientation = 2;
                } else if (mDefaultOrientation == 90) {
                    orientation = 3;
                } else if (mDefaultOrientation == 180) {
                    orientation = 0;
                } else {
                    orientation = 1;
                }
            } else {
                if (mDefaultOrientation == 90) {
                    orientation = 3;
                } else if (mDefaultOrientation == 270) {
                    orientation = 1;
                } else {
                    orientation = mDefaultOrientation / 90;
                }
            }
        }
        return orientation;
    }

    public void changeInputType() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
            }
        });
    }

    public void setDefaultEffect(Effect defaultEffect) {
        mDefaultEffect = defaultEffect;
    }

    //--------------------------------------???????????????????????????----------------------------------------

    @Override
    public void onMusicFilterTime(final long time) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "music_time", time);
            }
        });
    }

    @Override
    public void onEffectSelected(Effect effect) {
        if (effect == null) {
            return;
        }
        mDefaultEffect = effect;
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.removeMessages(ITEM_ARRAYS_EFFECT_INDEX);
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
                }
            });
        } else {
            mFuItemHandler.removeMessages(ITEM_ARRAYS_EFFECT_INDEX);
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
        }
    }

    @Override
    public void onFilterLevelSelected(float progress) {
        isNeedUpdateFaceBeauty = true;
        mFilterLevel = progress;
    }

    @Override
    public void onFilterNameSelected(String filterName) {
        isNeedUpdateFaceBeauty = true;
        sFilterName = filterName;
    }

    @Override
    public void onHairSelected(int type, int hairColorIndex, float hairColorLevel) {
        mHairColorIndex = hairColorIndex;
        mHairColorStrength = hairColorLevel;
        final int lastHairType = mHairColorType;
        mHairColorType = type;
        if (mHairColorType == lastHairType) {
            onHairLevelSelected(mHairColorType, mHairColorIndex, mHairColorStrength);
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mHairColorType == HAIR_NORMAL) {
                        mFuItemHandler.removeMessages(ITEM_ARRAYS_HAIR_NORMAL_INDEX);
                        mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_HAIR_NORMAL_INDEX);
                    } else if (mHairColorType == HAIR_GRADIENT) {
                        mFuItemHandler.removeMessages(ITEM_ARRAYS_HAIR_GRADIENT_INDEX);
                        mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_HAIR_GRADIENT_INDEX);
                    }
                }
            });
        }
    }

    @Override
    public void onHairLevelSelected(@HairType final int type, int hairColorIndex, float hairColorLevel) {
        mHairColorIndex = hairColorIndex;
        mHairColorStrength = hairColorLevel;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (type == HAIR_NORMAL) {
                    // ????????????
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX], "Index", mHairColorIndex);
                    // ????????????
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX], "Strength", mHairColorStrength);
                } else if (type == HAIR_GRADIENT) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX], "Index", mHairColorIndex);
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX], "Strength", mHairColorStrength);
                }
            }
        });
    }

    @Override
    public void onSkinDetectSelected(float isOpen) {
        isNeedUpdateFaceBeauty = true;
        mSkinDetect = isOpen;
    }

    @Override
    public void onBlurTypeSelected(float blurType) {
        mBlurType = blurType;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onBlurLevelSelected(float level) {
        mBlurLevel = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onColorLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mColorLevel = level;
    }


    @Override
    public void onRedLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mRedLevel = level;
    }

    @Override
    public void onEyeBrightSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeBright = level;
    }

    @Override
    public void onToothWhitenSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mToothWhiten = level;
    }

    @Override
    public void onEyeEnlargeSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeEnlarging = level;
    }

    @Override
    public void onCheekThinningSelected(float level) {
        mCheekThinning = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekNarrowSelected(float level) {
        // ?????????????????????0.5
        mCheekNarrow = level / 2;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekSmallSelected(float level) {
        // ?????????????????????0.5
        mCheekSmall = level / 2;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekVSelected(float level) {
        mCheekV = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityChinSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityChin = level;
    }

    @Override
    public void onIntensityForeheadSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityForehead = level;
    }

    @Override
    public void onIntensityNoseSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityNose = level;
    }

    @Override
    public void onIntensityMouthSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityMouth = level;
    }

    @Override
    public void onPosterTemplateSelected(final int tempWidth, final int tempHeight, final byte[] temp, final float[] landmark) {
        Arrays.fill(posterTemplateLandmark, 0);
        for (int i = 0; i < landmark.length; i++) {
            posterTemplateLandmark[i] = landmark[i];
        }
        // ??????????????????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_width", tempWidth);
        // ??????????????????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_height", tempHeight);
        // ?????????????????????75??????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_face_points", posterTemplateLandmark);
        // ??????????????? RGBA byte??????
        faceunity.fuCreateTexForItem(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "tex_template", temp, tempWidth, tempHeight);
    }

    @Override
    public void onPosterInputPhoto(final int inputWidth, final int inputHeight, final byte[] input, final float[] landmark) {
        Arrays.fill(posterPhotoLandmark, 0);
        for (int i = 0; i < landmark.length; i++) {
            posterPhotoLandmark[i] = landmark[i];
        }
        // ??????????????????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_width", inputWidth);
        // ??????????????????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_height", inputHeight);
        // ???????????????????????????75??????
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_face_points", posterPhotoLandmark);
        // ??????????????? RGBA byte ??????
        faceunity.fuCreateTexForItem(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "tex_input", input, inputWidth, inputHeight);
    }

    @Override
    public void setLivePhoto(final LivePhoto livePhoto) {
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIVE_PHOTO_INDEX, livePhoto));
                }
            });
        } else {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIVE_PHOTO_INDEX, livePhoto));
        }
    }

    @Override
    public void selectMakeupItem(final Map<String, Object> paramMap, final boolean removePrevious) {
        if (removePrevious) {
            final Map<String, Object> prevParams = new HashMap<>(mMakeupParams);
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int itemHandle = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                    if (itemHandle > 0) {
                        Set<Map.Entry<String, Object>> entries = prevParams.entrySet();
                        for (Map.Entry<String, Object> entry : entries) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            if (value instanceof double[]) {
                                double[] emp = new double[((double[]) value).length];
//                                Log.v(TAG, "clear: setParams double array:" + key + ", value:" + Arrays.toString(emp));
                                faceunity.fuItemSetParam(itemHandle, key, emp);
                            } else if (value instanceof Double) {
                                if (key.startsWith(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PREFIX)) {
//                                    Log.v(TAG, "clear: setParams double:" + key + ", 0.0");
                                    faceunity.fuItemSetParam(itemHandle, key, 0.0);
                                }
                            }
                        }
                    }
                }
            });
        }
        mMakeupParams.putAll(paramMap);
        if (mFuItemHandler != null) {
            mFuItemHandler.removeMessages(ITEM_ARRAYS_FACE_MAKEUP_INDEX);
            Message.obtain(mFuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, paramMap).sendToTarget();
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.removeMessages(ITEM_ARRAYS_FACE_MAKEUP_INDEX);
                    Message.obtain(mFuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, paramMap).sendToTarget();
                }
            });
        }
    }

    @Override
    public void setMakeupItemIntensity(final String name, final double density) {
        mMakeupParams.put(name, density);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX], name, density);
                } else {
                    selectMakeupItem(new HashMap<>(mMakeupParams), false);
                }
            }
        });
    }

    @Override
    public void setMakeupItemColor(final String name, final double[] colors) {
        mMakeupParams.put(name, colors);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX], name, colors);
                } else {
                    selectMakeupItem(new HashMap<>(mMakeupParams), false);
                }
            }
        });
    }

    @Override
    public void setBodySlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mBodySlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.BODY_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setLegSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mLegSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.LEG_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setWaistSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mWaistSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.WAIST_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setShoulderSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mShoulderSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.SHOULDER_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setHipSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mHipSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.HIP_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    public void loadAvatarBackground() {
        mNeedBackground = true;
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_AVATAR_BACKGROUND);
                }
            });
        } else {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_AVATAR_BACKGROUND);
        }
    }

    public void unloadAvatarBackground() {
        mNeedBackground = false;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0) {
                    faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                    mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                }
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param path ????????????????????????????????????
     */
    public void loadAvatarHair(final String path) {
        if (TextUtils.isEmpty(path)) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                        faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                        mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                    }
                }
            });
        } else {
            if (mFuItemHandler != null) {
                Message message = Message.obtain(mFuItemHandler, ITEM_ARRAYS_AVATAR_HAIR, path);
                mFuItemHandler.sendMessage(message);
            } else {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        Message message = Message.obtain(mFuItemHandler, ITEM_ARRAYS_AVATAR_HAIR, path);
                        mFuItemHandler.sendMessage(message);
                    }
                });
            }
        }
    }

    @Override
    public void onLightMakeupBatchSelected(List<MakeupItem> makeupItems) {
        Set<Integer> keySet = mLightMakeupItemMap.keySet();
        for (final Integer integer : keySet) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], MakeupParamHelper.getMakeupIntensityKeyByType(integer), 0);
                }
            });
        }
        mLightMakeupItemMap.clear();

        if (makeupItems != null && makeupItems.size() > 0) {
            for (int i = 0, size = makeupItems.size(); i < size; i++) {
                MakeupItem makeupItem = makeupItems.get(i);
                onLightMakeupSelected(makeupItem, makeupItem.getLevel());
            }
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], "is_makeup_on", 0);
                }
            });
        }
    }

    private void onLightMakeupSelected(final MakeupItem makeupItem, final float level) {
        int type = makeupItem.getType();
        MakeupItem mp = mLightMakeupItemMap.get(type);
        if (mp != null) {
            mp.setLevel(level);
        } else {
            // ????????????
            mLightMakeupItemMap.put(type, makeupItem.cloneSelf());
        }
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
                }
            });
        } else {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
        }
    }

    @Override
    public void onLightMakeupOverallLevelChanged(final float level) {
        Set<Map.Entry<Integer, MakeupItem>> entries = mLightMakeupItemMap.entrySet();
        for (final Map.Entry<Integer, MakeupItem> entry : entries) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], MakeupParamHelper.getMakeupIntensityKeyByType(entry.getKey()), level);
                    entry.getValue().setLevel(level);
                }
            });
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @param isCartoon
     */
    public void setIsCartoon(final boolean isCartoon) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_use_cartoon", isCartoon ? 1 : 0);
                }
            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param value ?????? [0-1]???0 ?????????
     */
    public void fixPosterFaceParam(final float value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "warp_intensity", value);
                }
            }
        });
    }

    //--------------------------------------IsTracking????????????????????????????????????----------------------------------------

    private int mTrackingStatus = 0;

    public interface OnTrackingStatusChangedListener {
        void onTrackingStatusChanged(int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    //--------------------------------------FaceUnitySystemError???faceunity?????????????????????????????????----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;


    //--------------------------------------OnBundleLoadCompleteListener???faceunity?????????????????????----------------------------------------

    public void setOnBundleLoadCompleteListener(OnBundleLoadCompleteListener onBundleLoadCompleteListener) {
        mOnBundleLoadCompleteListener = onBundleLoadCompleteListener;
    }

    /**
     * ?????? fuCreateItemFromPackage ????????????
     *
     * @param bundlePath ?????? bundle ?????????
     * @return ????????????????????? 0 ??????????????????
     */
    public int loadItem(String bundlePath) {
        if (TextUtils.isEmpty(bundlePath)) {
            return 0;
        }
        int item = 0;
        InputStream is = null;
        try {
            is = bundlePath.startsWith(Constant.filePath) ? new FileInputStream(new File(bundlePath)) : mContext.getAssets().open(bundlePath);
            byte[] itemData = new byte[is.available()];
            int len = is.read(itemData);
            item = faceunity.fuCreateItemFromPackage(itemData);
            Log.e(TAG, "bundle path: " + bundlePath + ", length: " + len + "Byte, handle:" + item);
        } catch (IOException e) {
            Log.e(TAG, "loadItem error ", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        return item;
    }

    //--------------------------------------FPS???FPS???????????????----------------------------------------

    private static final float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private static final float TIME = 5f;
    private int mCurrentFrameCnt = 0;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;
    private boolean mNeedBenchmark = true;
    private long mFuCallStartTime = 0;

    private OnFUDebugListener mOnFUDebugListener;

    public interface OnFUDebugListener {
        void onFpsChange(double fps, double renderTime);
    }

    private void benchmarkFPS() {
        if (!mNeedBenchmark) {
            return;
        }
        if (++mCurrentFrameCnt == TIME) {
            mCurrentFrameCnt = 0;
            long tmp = System.nanoTime();
            double fps = (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - mLastOneHundredFrameTimeStamp) / TIME));
            mLastOneHundredFrameTimeStamp = tmp;
            double renderTime = mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
            mOneHundredFrameFUTime = 0;

            if (mOnFUDebugListener != null) {
                mOnFUDebugListener.onFpsChange(fps, renderTime);
            }
        }
    }

    //--------------------------------------??????????????????????????????----------------------------------------

    public interface OnBundleLoadCompleteListener {
        /**
         * bundle ????????????
         *
         * @param what
         */
        void onBundleLoadComplete(int what);
    }

    @IntDef(value = {HAIR_NORMAL, HAIR_GRADIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HairType {
    }

    /**
     * ????????????????????????????????????
     *
     * @param itemHandle
     */
    private void updateEffectItemParams(Effect effect, final int itemHandle) {
        if (effect == null || itemHandle == 0) {
            return;
        }
        if (mIsInputImage == 1) {
            faceunity.fuItemSetParam(itemHandle, "isAndroid", 0.0);
        } else {
            faceunity.fuItemSetParam(itemHandle, "isAndroid", 1.0);
        }
        int effectType = effect.effectType();
        if (effectType == Effect.EFFECT_TYPE_NORMAL) {
            //rotationAngle ?????????????????????????????????
            faceunity.fuItemSetParam(itemHandle, "rotationAngle", 360 - mInputPropOrientation);
        }
        if (effectType == Effect.EFFECT_TYPE_BACKGROUND) {
            //???????????????????????????????????????????????????????????????
            faceunity.fuSetDefaultRotationMode((360 - mInputImageOrientation) / 90);
        }
        int back = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0;
        if (effectType == Effect.EFFECT_TYPE_AVATAR) {
            // Avatar ?????????????????????
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", back);
            setAvatarHairParams(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
        }

        if (effectType == Effect.EFFECT_TYPE_ANIMOJI || effectType == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            // ????????????
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", back);
            // ????????????
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", back);
            //?????????????????????????????????????????????????????????????????????animoji????????????????????????????????????
            faceunity.fuItemSetParam(itemHandle, "camera_change", 1.0);
            faceunity.fuSetDefaultRotationMode((360 - mInputImageOrientation) / 90);
        }

        if (effectType == Effect.EFFECT_TYPE_GESTURE) {
            //loc_y_flip???loc_x_flip ?????????????????????????????????????????????
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", back);
            faceunity.fuItemSetParam(itemHandle, "loc_y_flip", back);
            faceunity.fuItemSetParam(itemHandle, "loc_x_flip", back);
            faceunity.fuItemSetParam(itemHandle, "rotMode", mRotMode);
            int trackerOrientation = calculateFaceTrackerOrientation();
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "rotationMode", trackerOrientation);
        }

        if (effectType == Effect.EFFECT_TYPE_ANIMOJI) {
            // ?????????????????????????????????
            faceunity.fuItemSetParam(itemHandle, "isFlipTrack", back);
            // ????????????
            faceunity.fuItemSetParam(itemHandle, "isFlipLight ", back);
            // ?????? Animoji ????????????
            faceunity.fuItemSetParam(itemHandle, "{\"thing\":\"<global>\",\"param\":\"follow\"}", 1);
        }
        setMaxFaces(effect.maxFace());
    }

    private void setAvatarHairParams(int itemAvatarHair) {
        if (itemAvatarHair > 0) {
            int back = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0;
            faceunity.fuItemSetParam(itemAvatarHair, "is3DFlipH", back);
            faceunity.fuItemSetParam(itemAvatarHair, "isFlipTrack", back);
        }
    }

    /*----------------------------------Builder---------------------------------------*/

    /**
     * FURenderer Builder
     */
    public static class Builder {

        private boolean createEGLContext = false;
        private Effect defaultEffect;
        private int maxFaces = 1;
        private Context context;
        private int inputTextureType = 0;
        private int inputImageFormat = 0;
        private int inputImageRotation = 270;
        private int inputPropRotation = 270;
        private int isIputImage = 0;
        private boolean isNeedAnimoji3D = false;
        private boolean isNeedBeautyHair = false;
        private boolean isNeedFaceBeauty = true;
        private boolean isNeedPosterFace = false;
        private int filterStyle = CartoonFilter.NO_FILTER;
        private int currentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
        private OnBundleLoadCompleteListener onBundleLoadCompleteListener;
        private OnFUDebugListener onFUDebugListener;
        private OnTrackingStatusChangedListener onTrackingStatusChangedListener;
        private OnSystemErrorListener onSystemErrorListener;
        private boolean useBeautifyBody = false;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * ????????????????????????EGLContext
         *
         * @param createEGLContext
         * @return
         */
        public Builder createEGLContext(boolean createEGLContext) {
            this.createEGLContext = createEGLContext;
            return this;
        }

        /**
         * ??????????????????????????????
         *
         * @param defaultEffect
         * @return
         */
        public Builder defaultEffect(Effect defaultEffect) {
            this.defaultEffect = defaultEffect;
            return this;
        }


        /**
         * ????????????????????????
         *
         * @param isIputImage
         * @return
         */
        public Builder inputIsImage(int isIputImage) {
            this.isIputImage = isIputImage;
            return this;
        }

        /**
         * ?????????????????????
         *
         * @param maxFaces
         * @return
         */
        public Builder maxFaces(int maxFaces) {
            this.maxFaces = maxFaces;
            return this;
        }

        /**
         * ??????????????????
         *
         * @param useBeautBody
         * @return
         */
        public Builder setUseBeautifyBody(boolean useBeautBody) {
            this.useBeautifyBody = useBeautBody;
            return this;
        }

        /**
         * ??????????????????????????????????????????????????????????????????
         * camera OES?????????1
         * ??????2D?????????2
         *
         * @param textureType
         * @return
         */
        public Builder inputTextureType(int textureType) {
            this.inputTextureType = textureType;
            return this;
        }

        /**
         * ?????????byte[]????????????
         *
         * @param inputImageFormat
         * @return
         */
        public Builder inputImageFormat(int inputImageFormat) {
            this.inputImageFormat = inputImageFormat;
            return this;
        }

        /**
         * ???????????????????????????
         *
         * @param inputImageRotation
         * @return
         */
        public Builder inputImageOrientation(int inputImageRotation) {
            this.inputImageRotation = inputImageRotation;
            return this;
        }

        /**
         * ????????????
         *
         * @param inputPropRotation
         * @return
         */
        public Builder inputPropOrientation(int inputPropRotation) {
            this.inputPropRotation = inputPropRotation;
            return this;
        }

        /**
         * ????????????3D????????????????????????
         *
         * @param needAnimoji3D
         * @return
         */
        public Builder setNeedAnimoji3D(boolean needAnimoji3D) {
            this.isNeedAnimoji3D = needAnimoji3D;
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param needBeautyHair
         * @return
         */
        public Builder setNeedBeautyHair(boolean needBeautyHair) {
            isNeedBeautyHair = needBeautyHair;
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param needFaceBeauty
         * @return
         */
        public Builder setNeedFaceBeauty(boolean needFaceBeauty) {
            isNeedFaceBeauty = needFaceBeauty;
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param filterStyle
         * @return
         */
        public Builder setFilterStyle(int filterStyle) {
            this.filterStyle = filterStyle;
            return this;
        }

        /**
         * ????????????????????????
         *
         * @param needPosterFace
         * @return
         */
        public Builder setNeedPosterFace(boolean needPosterFace) {
            isNeedPosterFace = needPosterFace;
            return this;
        }

        /**
         * ??????????????????????????????????????????
         *
         * @param cameraType
         * @return
         */
        public Builder setCurrentCameraType(int cameraType) {
            currentCameraType = cameraType;
            return this;
        }

        /**
         * ??????debug????????????
         *
         * @param onFUDebugListener
         * @return
         */
        public Builder setOnFUDebugListener(OnFUDebugListener onFUDebugListener) {
            this.onFUDebugListener = onFUDebugListener;
            return this;
        }

        /**
         * ????????????????????????????????????
         *
         * @param onTrackingStatusChangedListener
         * @return
         */
        public Builder setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener onTrackingStatusChangedListener) {
            this.onTrackingStatusChangedListener = onTrackingStatusChangedListener;
            return this;
        }

        /**
         * ??????bundle??????????????????
         *
         * @param onBundleLoadCompleteListener
         * @return
         */
        public Builder setOnBundleLoadCompleteListener(OnBundleLoadCompleteListener onBundleLoadCompleteListener) {
            this.onBundleLoadCompleteListener = onBundleLoadCompleteListener;
            return this;
        }


        /**
         * ??????SDK??????????????????
         *
         * @param onSystemErrorListener
         * @return
         */
        public Builder setOnSystemErrorListener(OnSystemErrorListener onSystemErrorListener) {
            this.onSystemErrorListener = onSystemErrorListener;
            return this;
        }

        public FURenderer build() {
            FURenderer fuRenderer = new FURenderer(context, createEGLContext);
            fuRenderer.mMaxFaces = maxFaces;
            fuRenderer.mInputTextureType = inputTextureType;
            fuRenderer.mInputImageFormat = inputImageFormat;
            fuRenderer.mInputImageOrientation = inputImageRotation;
            fuRenderer.mInputPropOrientation = inputPropRotation;
            fuRenderer.mIsInputImage = isIputImage;
            fuRenderer.mDefaultEffect = defaultEffect;
            fuRenderer.isNeedAnimoji3D = isNeedAnimoji3D;
            fuRenderer.isNeedBeautyHair = isNeedBeautyHair;
            fuRenderer.isNeedFaceBeauty = isNeedFaceBeauty;
            fuRenderer.isNeedPosterFace = isNeedPosterFace;
            fuRenderer.mCurrentCameraType = currentCameraType;
            fuRenderer.mComicFilterStyle = filterStyle;
            fuRenderer.mUseBeautifyBody = useBeautifyBody;

            fuRenderer.mOnFUDebugListener = onFUDebugListener;
            fuRenderer.mOnTrackingStatusChangedListener = onTrackingStatusChangedListener;
            fuRenderer.mOnSystemErrorListener = onSystemErrorListener;
            fuRenderer.mOnBundleLoadCompleteListener = onBundleLoadCompleteListener;
            return fuRenderer;
        }
    }

    /**
     * ??????????????????
     */
    public class BeautifyBodyParam {
        /**
         * 0.0~1.0????????????????????????????????????0.0????????????
         */
        public static final String BODY_SLIM_STRENGTH = "BodySlimStrength";
        /**
         * 0.0~1.0???????????????????????????????????????0.0????????????
         */
        public static final String LEG_SLIM_STRENGTH = "LegSlimStrength";
        /**
         * 0.0~1.0????????????????????????????????????0.0????????????
         */
        public static final String WAIST_SLIM_STRENGTH = "WaistSlimStrength";
        /**
         * 0.0~1.0?????????0.5?????????????????????0.5???????????????0.5????????????
         */
        public static final String SHOULDER_SLIM_STRENGTH = "ShoulderSlimStrength";
        /**
         * 0.0~1.0??????????????????????????????????????????0.0????????????
         */
        public static final String HIP_SLIM_STRENGTH = "HipSlimStrength";
        /**
         * ????????????????????????
         */
        public static final String CLEAR_SLIM = "ClearSlim";
        /**
         * ?????????????????? 0, 1, 2, 3
         */
        public static final String ORIENTATION = "Orientation";
        /**
         * ?????? 0.0 ?????? 1.0,  0.0 ????????????????????????1.0 ????????????????????????
         */
        public static final String DEBUG = "Debug";
    }

    /**
     * Avatar ??????????????????
     */
    static class AvatarConstant {
        public static final int EXPRESSION_LENGTH = 46;
        public static final float[] ROTATION_DATA = new float[]{0f, 0f, 0f, 1f};
        public static final float[] PUP_POS_DATA = new float[]{0f, 0f};
        public static final int VALID_DATA = 1;
        public static final float[] EXPRESSIONS = new float[EXPRESSION_LENGTH];
    }

//--------------------------------------Builder----------------------------------------

    class FUItemHandler extends Handler {

        FUItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //?????????????????? bundle
                case ITEM_ARRAYS_EFFECT_INDEX: {
                    final Effect effect = (Effect) msg.obj;
                    if (effect == null) {
                        return;
                    }
                    boolean isNone = effect.effectType() == Effect.EFFECT_TYPE_NONE;
                    final int itemEffect = isNone ? 0 : loadItem(effect.path());
                    if (!isNone && itemEffect <= 0) {
                        Log.w(TAG, "create effect item failed: " + itemEffect);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                                mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = 0;
                            }
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0 && !mNeedBackground) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                            }
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                            }
                            if (itemEffect > 0) {
                                updateEffectItemParams(effect, itemEffect);
                            }
                            mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = itemEffect;
                        }
                    });
                }
                break;
                //???????????? bundle
                case ITEM_ARRAYS_FACE_BEAUTY_INDEX: {
                    final int itemBeauty = loadItem(BUNDLE_FACE_BEAUTIFICATION);
                    if (itemBeauty <= 0) {
                        Log.w(TAG, "load face beauty item failed: " + itemBeauty);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX]);
                                mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;
                            isNeedUpdateFaceBeauty = true;
                        }
                    });
                }
                break;
                // ??????????????? bundle
                case ITEM_ARRAYS_LIGHT_MAKEUP_INDEX: {
                    final MakeupItem makeupItem = (MakeupItem) msg.obj;
                    if (makeupItem == null) {
                        return;
                    }
                    String path = makeupItem.getPath();
                    if (!TextUtils.isEmpty(path)) {
                        if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] <= 0) {
                            int itemLightMakeup = loadItem(BUNDLE_LIGHT_MAKEUP);
                            if (itemLightMakeup <= 0) {
                                Log.w(TAG, "create light makeup item failed: " + itemLightMakeup);
                                return;
                            }
                            mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] = itemLightMakeup;
                        }
                        MakeupParamHelper.TextureImage textureImage = null;
                        if (makeupItem.getType() == FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK) {
                            mLipStickColor = MakeupParamHelper.readRgbaColor(mContext, path);
                        } else {
                            textureImage = MakeupParamHelper.createTextureImage(mContext, path);
                        }
                        final MakeupParamHelper.TextureImage finalTextureImage = textureImage;
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int itemHandle = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.IS_MAKEUP_ON, 1.0);
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY, 1.0);
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.REVERSE_ALPHA, 1.0);
                                if (mLipStickColor != null) {
                                    if (makeupItem.getType() == FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK) {
                                        faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, mLipStickColor);
                                        faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_LIP_MASK, 1.0);
                                    }
                                } else {
                                    faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP, 0.0);
                                }
                                if (finalTextureImage != null) {
                                    String key = MakeupParamHelper.getMakeupTextureKeyByType(makeupItem.getType());
                                    faceunity.fuCreateTexForItem(itemHandle, key, finalTextureImage.getBytes(), finalTextureImage.getWidth(), finalTextureImage.getHeight());
                                }
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.getMakeupIntensityKeyByType(makeupItem.getType()), makeupItem.getLevel());
                            }
                        });
                    } else {
                        // ????????????
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
                                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX],
                                            MakeupParamHelper.getMakeupIntensityKeyByType(makeupItem.getType()), 0.0);
                                }
                            }
                        });
                    }
                }
                break;
                // ???????????? bundle
                case ITEM_ARRAYS_FACE_MAKEUP_INDEX: {
                    final Map<String, Object> paramMap = (Map<String, Object>) msg.obj;
                    if (paramMap == null) {
                        return;
                    }
                    // ???????????? 209 ????????????????????????
                    if (mItemsArray[ITEM_ARRAYS_NEW_FACE_TRACKER] <= 0) {
                        int itemNewFaceTracker = loadItem(BUNDLE_NEW_FACE_TRACKER);
                        if (itemNewFaceTracker <= 0) {
                            Log.w(TAG, "create new face tracker item failed: " + itemNewFaceTracker);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_NEW_FACE_TRACKER] = itemNewFaceTracker;
                        setFaceTrackerOrientation(calculateFaceTrackerOrientation());
                    }
                    if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] <= 0) {
                        int itemFaceMakeup = loadItem(BUNDLE_FACE_MAKEUP);
                        if (itemFaceMakeup <= 0) {
                            Log.w(TAG, "create face makeup item failed: " + itemFaceMakeup);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] = itemFaceMakeup;
                    }
                    final Set<Map.Entry<String, Object>> paramEntries = paramMap.entrySet();
                    final Map<String, MakeupParamHelper.TextureImage> texImageMap = new HashMap<>(16);
                    for (Map.Entry<String, Object> entry : paramEntries) {
                        Object value = entry.getValue();
                        if (value instanceof String) {
//                            Log.v(TAG, "createTextureImage: " + value);
                            MakeupParamHelper.TextureImage texParams = MakeupParamHelper.createTextureImage(mContext, (String) value);
                            if (texParams != null) {
                                texImageMap.put(entry.getKey(), texParams);
                            }
                        }
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            int itemHandle = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                            faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.IS_MAKEUP_ON, 1.0);
                            faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.REVERSE_ALPHA, 1.0);
                            faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_LIP_MASK, 1.0);
                            faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY, 1.0);
                            for (Map.Entry<String, Object> entry : paramEntries) {
                                Object value = entry.getValue();
                                String key = entry.getKey();
                                if (value instanceof double[]) {
                                    double[] val = (double[]) value;
//                                    Log.v(TAG, "run: setParams double array:" + key + ", value:" + Arrays.toString(val));
                                    faceunity.fuItemSetParam(itemHandle, key, val);
                                } else if (value instanceof Double) {
                                    Double val = (Double) value;
//                                    Log.v(TAG, "run: setParams double:" + key + ", value:" + val);
                                    faceunity.fuItemSetParam(itemHandle, key, val);
                                }
                            }
                            Set<Map.Entry<String, MakeupParamHelper.TextureImage>> texEntries = texImageMap.entrySet();
                            for (Map.Entry<String, MakeupParamHelper.TextureImage> texEntry : texEntries) {
                                MakeupParamHelper.TextureImage value = texEntry.getValue();
                                String key = texEntry.getKey();
//                                Log.v(TAG, "run: setParams:" + key + ", tex:" + value);
                                faceunity.fuCreateTexForItem(itemHandle, key, value.getBytes(), value.getWidth(), value.getHeight());
                            }
                        }
                    });
                }
                break;
                //?????????????????? bundle
                case ITEM_ARRAYS_HAIR_NORMAL_INDEX: {
                    final int itemHair = loadItem(BUNDLE_HAIR_NORMAL);
                    if (itemHair <= 0) {
                        Log.w(TAG, "create hair normal item failed: " + itemHair);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX]);
                                mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX] = 0;
                            }
                            if (mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX]);
                                mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX] = 0;
                            }
                            // ????????????
                            faceunity.fuItemSetParam(itemHair, "Index", mHairColorIndex);
                            // ????????????
                            faceunity.fuItemSetParam(itemHair, "Strength", mHairColorStrength);
                            mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX] = itemHair;
                        }
                    });
                }
                break;
                // ?????????????????? bundle
                case ITEM_ARRAYS_HAIR_GRADIENT_INDEX: {
                    final int itemHair = loadItem(BUNDLE_HAIR_GRADIENT);
                    if (itemHair <= 0) {
                        Log.w(TAG, "create hair gradient item failed: " + itemHair);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX]);
                                mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX] = 0;
                            }
                            if (mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX]);
                                mItemsArray[ITEM_ARRAYS_HAIR_NORMAL_INDEX] = 0;
                            }
                            faceunity.fuItemSetParam(itemHair, "Index", mHairColorIndex);
                            faceunity.fuItemSetParam(itemHair, "Strength", mHairColorStrength);
                            mItemsArray[ITEM_ARRAYS_HAIR_GRADIENT_INDEX] = itemHair;
                        }
                    });
                }
                break;
                // ?????????????????? bundle
                case ITEM_ARRAYS_HAIR_MODEL: {
                    final int itemModel = loadItem(BUNDLE_HAIR_MODEL_HIGH_SPEED);
                    if (itemModel < 0) {
                        Log.w(TAG, "create hair model failed: " + itemModel);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_HAIR_MODEL] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_HAIR_MODEL]);
                                mItemsArray[ITEM_ARRAYS_HAIR_MODEL] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_HAIR_MODEL] = itemModel;
                        }
                    });
                }
                break;
                // ?????? Animoji ???????????? bundle
                case ITEM_ARRAYS_CARTOON_FILTER_INDEX: {
                    final int style = (int) msg.obj;
                    if (style >= 0) {
                        if (mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] <= 0) {
                            int itemCartoonFilter = loadItem(BUNDLE_CARTOON_FILTER);
                            if (itemCartoonFilter <= 0) {
                                Log.w(TAG, "create cartoon filter item failed: " + itemCartoonFilter);
                                return;
                            }
                            mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = itemCartoonFilter;
                        }
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX], "style", style);
                                GlUtil.logVersionInfo();
                                int glMajorVersion = GlUtil.getGlMajorVersion();
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX], "glVer", glMajorVersion);
                            }
                        });
                    } else {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] > 0) {
                                    faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX]);
                                    mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = 0;
                                }
                            }
                        });
                    }
                }
                break;
                // ?????????????????? bundle
                case ITEM_ARRAYS_LIVE_PHOTO_INDEX: {
                    final LivePhoto livePhoto = (LivePhoto) msg.obj;
                    if (livePhoto == null) {
                        return;
                    }
                    if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] <= 0) {
                        int itemLivePhoto = loadItem(BUNDLE_LIVE_PHOTO);
                        if (itemLivePhoto <= 0) {
                            Log.w(TAG, "create live photo item failed: " + itemLivePhoto);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] = itemLivePhoto;
                    }
                    setIsFrontCamera(mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT);

                    final MakeupParamHelper.TextureImage textureImage = MakeupParamHelper.createTextureImage(mContext, livePhoto.getTemplateImagePath());
                    if (textureImage != null) {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int itemHandle = mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX];
                                // ??????????????????
                                faceunity.fuItemSetParam(itemHandle, "group_type", livePhoto.getGroupType());
                                // ??????????????????
                                faceunity.fuItemSetParam(itemHandle, "group_points", livePhoto.getGroupPoints());
                                // ??????????????????
                                faceunity.fuItemSetParam(itemHandle, "target_width", textureImage.getWidth());
                                // ??????????????????
                                faceunity.fuItemSetParam(itemHandle, "target_height", textureImage.getHeight());
                                // ??????????????? RGBA byte ??????
                                faceunity.fuCreateTexForItem(itemHandle, "tex_input", textureImage.getBytes(), textureImage.getWidth(), textureImage.getHeight());
                                // ??????????????????
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "use_interpolate2", 0.0);
                                // ??????????????????????????????
                                setIsCartoon(true);
                            }
                        });
                    }
                }
                break;
                // ??????
                case ITEM_ARRAYS_BEAUTIFY_BODY: {
                    final int itemBeautifyBody = loadItem(BUNDLE_BEAUTIFY_BODY);
                    if (itemBeautifyBody <= 0) {
                        Log.w(TAG, "create beautify body item failed: " + itemBeautifyBody);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY]);
                                mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] = 0;
                            }
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.BODY_SLIM_STRENGTH, mBodySlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.LEG_SLIM_STRENGTH, mLegSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.WAIST_SLIM_STRENGTH, mWaistSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.SHOULDER_SLIM_STRENGTH, mShoulderSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.HIP_SLIM_STRENGTH, mHipSlimStrength);
                            int orientation;
                            if (mIsInputImage == 1) {
                                orientation = 0;
                            } else {
                                orientation = calculateFaceTrackerOrientation();
                            }
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.DEBUG, 0.0);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.ORIENTATION, orientation);
                            mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] = itemBeautifyBody;
                        }
                    });
                }
                break;
                // ?????? Avatar ??????????????? bundle
                case ITEM_ARRAYS_AVATAR_HAIR: {
                    String path = (String) msg.obj;
                    if (!TextUtils.isEmpty(path)) {
                        final int itemAvatarHair = loadItem(path);
                        if (itemAvatarHair <= 0) {
                            Log.w(TAG, "create avatar hair item failed: " + itemAvatarHair);
                            return;
                        }
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int oldItem = mItemsArray[ITEM_ARRAYS_AVATAR_HAIR];
                                setAvatarHairParams(itemAvatarHair);
                                mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = itemAvatarHair;
                                if (oldItem > 0) {
                                    faceunity.fuDestroyItem(oldItem);
                                }
                            }
                        });
                    } else {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                                    faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                                    mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                                }
                            }
                        });
                    }
                }
                break;
                // ?????? Avatar ??????????????? bundle
                case ITEM_ARRAYS_AVATAR_BACKGROUND: {
                    final int itemAvatarBg = loadItem(BUNDLE_AVATAR_BACKGROUND);
                    if (itemAvatarBg <= 0) {
                        Log.w(TAG, "create avatar background item failed: " + itemAvatarBg);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = itemAvatarBg;
                        }
                    });
                }
                break;
                // ?????? Animoji ??????3D????????? bundle
                case ITEM_ARRAYS_ABIMOJI_3D_INDEX: {
                    final int itemAnimoji3D = loadItem(BUNDLE_FXAA);
                    if (itemAnimoji3D <= 0) {
                        Log.w(TAG, "create Animoji3D item failed: " + itemAnimoji3D);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX]);
                                mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] = itemAnimoji3D;
                        }
                    });
                }
                break;
                default:
            }
            if (mOnBundleLoadCompleteListener != null) {
                mOnBundleLoadCompleteListener.onBundleLoadComplete(msg.what);
            }
        }
    }
}
