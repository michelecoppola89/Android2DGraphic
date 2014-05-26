package com.example.android2dgraphic;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import android.widget.Toast;

import java.lang.Math;

class SquareRenderer implements GLSurfaceView.Renderer {
	private boolean mTranslucentBackground;
	private Square[] mSquare = new Square[3];
	private CannonBall[] mCannonBall = new CannonBall[1];

	private float mTransX = 0.0f;
	private float mTransY = 0.0f;

	public float mTransAngle;
	private Context context;

	private Vector2D touchPos = new Vector2D();
	private Vector2D cannonPos = new Vector2D(0.0f, 0.0f);

	Vector2D ballPos = new Vector2D(0, 0);
	Vector2D ballVelocity = new Vector2D(0, 0);
	Vector2D gravity = new Vector2D(0, -10);

	float FRUSTUM_WIDTH = 10f;
	float FRUSTUM_HEIGHT = 10f;

	private float vertices1[] = {

	-1.0f, -1.0f, 0.0f, // V1 - bottom left

			-1.0f, 1.0f, 0.0f, // V2 - top left

			1.0f, -1.0f, 0.0f, // V3 - bottom right

			1.0f, 1.0f, 0.0f // V4 - top right
	};

	public SquareRenderer(boolean useTranslucentBackground, Context c) {
		mTranslucentBackground = useTranslucentBackground;
		context = c;
		mSquare[0] = new Square(vertices1);
		mCannonBall[0] = new CannonBall(vertices1);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glTranslatef(mTransX, mTransY, 0);
		mCannonBall[0].draw(gl);

		gl.glLoadIdentity();
		gl.glRotatef(mTransAngle, 0, 0, 5);
		mSquare[0].draw(gl);

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		mTransX += ballVelocity.x;
		mTransY += ballVelocity.y;

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-5.0f, 5.0f, -5.0f, 5.0f, 1, -1);// parallel projection
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		// Load the texture for the square
		mSquare[0].loadGLTexture(gl, this.context);
		mCannonBall[0].loadGLTexture(gl, this.context);

		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	public void update(float x, float y, GLSurfaceView Gview, MotionEvent event) {

		touchPos.x = ((x / (float) Gview.getWidth()) * FRUSTUM_WIDTH)-5;
		touchPos.y = ((1 - y / (float) Gview.getHeight()) * FRUSTUM_HEIGHT)-5;
		mTransAngle = touchPos.sub(cannonPos).angle();

		if (event.getAction() == MotionEvent.ACTION_UP) {
			float radians = mTransAngle * Vector2D.TO_RADIANS;
			float ballSpeed = 0.05f;
			ballPos.set(touchPos);
			ballVelocity.x = FloatMath.cos(radians) * ballSpeed;
			ballVelocity.y = FloatMath.sin(radians) * ballSpeed;
		}

	}
}