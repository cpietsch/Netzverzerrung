import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL;

import processing.core.PApplet;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics2d.behaviors.GravityBehavior2D;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;
import controlP5.Button;
import controlP5.ColorPicker;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.ControlWindow;
import controlP5.Controller;
import controlP5.Toggle;

/**
 * Mesh1 is a quick sketch to try out the toxiclibs springs in a pseudo mesh.
 * you got several options to customize the visual output. use tongseng or
 * tuiosimulator to add more than one attractor.
 * 
 * check out the video: http://vimeo.com/34704996
 * 
 * @author Christopher Pietsch
 */

public class mesh1 extends PApplet implements TuioListener {

	public static void main(String[] args) {

		// log.setLevel(Level.OFF);
		/*
		 * int primary_display = 0; //index into Graphic Devices array...
		 * 
		 * int primary_width; int primary_height;
		 * 
		 * GraphicsEnvironment environment =
		 * GraphicsEnvironment.getLocalGraphicsEnvironment(); GraphicsDevice
		 * devices[] = environment.getScreenDevices(); String location;
		 * println(devices[1].getDisplayMode().getWidth()); if(devices.length>1
		 * ){ //we have a 2nd display/projector
		 * if(devices[0].getDisplayMode().getWidth()==800){ //monitor:right
		 * (surface) primary_width = -devices[0].getDisplayMode().getWidth();
		 * location = "--location="+primary_width+",0";
		 * 
		 * } else{ //monitor: bottom primary_height =
		 * devices[0].getDisplayMode().getHeight(); location = "--location=0,"+
		 * primary_height; } }else{//leave on primary display location =
		 * "--location=0,0";
		 * 
		 * } String display = "--display="+primary_display+1; //processing
		 * considers the first display to be # 1 // PApplet.main(new String[] {
		 * location , "--hide-stop", display,"mesh1" }); // Der letzte String
		 * muss der Pfad zu Deiner Klasse sein
		 */
		// PApplet.main(new String[] { "--location=1440,0" , "--hide-stop",
		// "--display=2","mesh1" });
		PApplet.main(new String[] { "--location=1640,100", "mesh1" });

	}

	TuioClient tuioClient;
	int DIM = 50;
	int SPACE = 20;
	float SPRING_STRENGTH = 0.125f;
	float INNER_STRENGTH = 0.13f;
	int SPRING_REST = 10;
	int TRANSPARENT = 10;
	int FILL_LINES = 10;
	int FILL_POINTS = 10;
	boolean ATTRACTION_MODE;

	int DIMX;
	int DIMY;

	float LINE_WEIGHT = 1;
	VerletPhysics2D physics;

	VerletParticle2D head, tail;
	AttractionBehavior2D mouseAttractor;

	// ArrayList<Particle> particles;
	Vec2D mousePos;
	ControlP5 controlP5;
	ControlWindow controlWindow;

	Controller myFrames;

	int timeStamp = 0;
	boolean bang = false;

	boolean isSavingFrame = false;
	GL gl;
	private float mouseRadius;
	private float mouseStrength;
	private float WORLD_DRAG;

	private final boolean presentMode = true;
	List<AttractionBehavior2D> attractors;

	HashMap<Integer, AttractionBehavior2D> attractorMap = new HashMap<Integer, AttractionBehavior2D>();
	private boolean POINT_ANI;

	// called when a cursor is added to the scene
	@Override
	public void addTuioCursor(TuioCursor tcur) {
		println("add cursor " + tcur.getCursorID() + " (" + tcur.getSessionID()
				+ ") " + tcur.getX() + " " + tcur.getY());

		AttractionBehavior2D a = new AttractionBehavior2D(new Vec2D(
				tcur.getScreenX(width), tcur.getScreenY(height)), mouseRadius,
				(ATTRACTION_MODE == true ? -1 : 1) * mouseStrength);
		physics.addBehavior(a);
		attractorMap.put(tcur.getCursorID(), a);

		// attractors.add(a);
		// println(attractors.indexOf(a));

		// mousePos = new Vec2D(tcur.getScreenX(width),
		// tcur.getScreenY(height));
		// mouseAttractor = new AttractionBehavior(mousePos, mouseRadius,
		// -1*mouseStrength);
		// physics.addBehavior( mouseAttractor );
	}

	// called when an object is added to the scene
	@Override
	public void addTuioObject(TuioObject tobj) {

	}

	@Override
	public void draw() {

		// background(0,10);
		fill(0, TRANSPARENT);
		rect(0, 0, width, height);

		// head.set(mouseX,mouseY);
		if (frameCount > timeStamp + 10) {
			physics.update();

			strokeWeight(LINE_WEIGHT);

			// beginShape(LINES);
			// stroke(255, FILL_LINES);
			for (VerletSpring2D s : physics.springs) {
				int c = 255 - (int) s.a.distanceTo(s.b) * 5;
				stroke(c, (s.a.x / width) * 200, 200, FILL_LINES);
				// noStroke();
				// stroke(50 + c / 5, s.a.x / 100, c, FILL_LINES);
				// stroke(150 + c / 5, 100, c, FILL_LINES);
				line(s.a.x, s.a.y, s.b.x, s.b.y);
				// vertex(s.a.x, s.a.y);
				// vertex(s.b.x, s.b.y);
			}
			// endShape();

			int fill;

			VerletParticle2D p;

			for (int y = 0, idx = 0; y < DIMY; y++) {
				for (int x = 0; x < DIMX; x++) {
					fill = FILL_POINTS;

					if (POINT_ANI) {
						if (x == (frameCount % (DIMX))) {
							fill = 255;
						}
					}
					stroke(255, fill);
					p = physics.particles.get(idx);
					point(p.x, p.y);
					idx++;
				}
			}

			// for (VerletParticle2D p : physics.particles) {
			// fill = FILL_POINTS;
			// if (count == run) {
			// fill = 255;
			//
			// }
			// // println(fill);
			// stroke(255, fill);
			// point(p.x, p.y);
			// count++;
			// }
			/*
			 * pushMatrix(); translate(1,1,+100); stroke(255,40);
			 * for(VerletSpring2D s: physics.springs){
			 * line(s.a.x,s.a.y,s.b.x,s.b.y); } popMatrix();
			 * 
			 * pushMatrix(); translate(1,1,+200); stroke(255,20);
			 * for(VerletSpring2D s: physics.springs){
			 * line(s.a.x,s.a.y,s.b.x,s.b.y); } popMatrix();
			 */

		} else {

		}

		if (bang) {
			bang = false;
			initPhysics();
		}

		if (isSavingFrame) {
			saveFrame("isocontour-frame-####.png");
			// println( "iso num vertices: " + iso.numVertices );
			// println( "fps: " + (int)frameRate );
		}

		// println("fps: " + (int) frameRate);
	}

	public void draw2() {

		// background(0,10);
		fill(0, TRANSPARENT);
		rect(0, 0, width, height);

		// head.set(mouseX,mouseY);
		if (frameCount > timeStamp + 10) {
			physics.update();

			strokeWeight(LINE_WEIGHT);
			// stroke(255, FILL_LINES);
			for (VerletSpring2D s : physics.springs) {
				int c = (int) s.a.distanceTo(s.b) * 4;
				stroke(c, s.a.x / 100, 100, FILL_LINES);
				// stroke(50 + c / 5, s.a.x / 100, c, FILL_LINES);
				// stroke(150 + c / 5, 100, c, FILL_LINES);
				line(s.a.x, s.a.y, s.b.x, s.b.y);
			}

			stroke(255, FILL_POINTS);
			for (VerletParticle2D p : physics.particles) {
				point(p.x, p.y);
			}
			/*
			 * pushMatrix(); translate(1,1,+100); stroke(255,40);
			 * for(VerletSpring2D s: physics.springs){
			 * line(s.a.x,s.a.y,s.b.x,s.b.y); } popMatrix();
			 * 
			 * pushMatrix(); translate(1,1,+200); stroke(255,20);
			 * for(VerletSpring2D s: physics.springs){
			 * line(s.a.x,s.a.y,s.b.x,s.b.y); } popMatrix();
			 */

		} else {

		}

		if (bang) {
			bang = false;
			initPhysics();
		}

		if (isSavingFrame) {
			saveFrame("isocontour-frame-####.png");
			// println( "iso num vertices: " + iso.numVertices );
			// println( "fps: " + (int)frameRate );
		}
	}

	@Override
	public void init() {
		// setWindowBorder();
		super.init();
	}

	private void initP5() {
		// TODO Auto-generated method stub

		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
		// controlP5.setAutoInitialization(true);

		controlWindow = controlP5.addControlWindow("controlP5window", 700, 0,
				400, 400);
		controlWindow.hideCoordinates();
		controlWindow.setBackground(color(40));

		Button btAddAttractor;
		for (int i = 0; i < 10; i++) {
			btAddAttractor = controlP5.addButton(i + "s", 0, 10 + i * 22, 10,
					20, 20);
			// btAddAttractor.setLabel("add attractor");
			btAddAttractor.addListener(new ControlListener() {
				@Override
				public void controlEvent(ControlEvent e) {
					controlP5.saveProperties(("mesh2d"
							+ e.getName().substring(0, 1) + ".ser"));
				}
			});
			btAddAttractor.setWindow(controlWindow);
		}

		for (int i = 0; i < 10; i++) {
			btAddAttractor = controlP5.addButton(i + "l", 0, 10 + i * 22, 30,
					20, 20);
			// btAddAttractor.setLabel("add attractor");
			btAddAttractor.addListener(new ControlListener() {
				@Override
				public void controlEvent(ControlEvent e) {
					controlP5.loadProperties(("mesh2d"
							+ e.getName().substring(0, 1) + ".ser"));
					bang = true;
				}
			});
			btAddAttractor.setWindow(controlWindow);
		}
		/*
		 * Button btAddAttractor = controlP5.addButton("Save", 0, 10, 10,
		 * 50,20); //btAddAttractor.setLabel("add attractor");
		 * btAddAttractor.addListener(new ControlListener() {
		 * 
		 * @Override public void controlEvent(ControlEvent e) {
		 * controlP5.saveProperties(("mesh1.ser")); } });
		 * btAddAttractor.setWindow(controlWindow);
		 * 
		 * Button btAddAttractor2 = controlP5.addButton("Load", 0, 70, 10,
		 * 50,20); //btAddAttractor.setLabel("add attractor");
		 * btAddAttractor2.addListener(new ControlListener() {
		 * 
		 * @Override public void controlEvent(ControlEvent e) {
		 * controlP5.loadProperties(("mesh1.ser")); } });
		 * btAddAttractor2.setWindow(controlWindow);
		 */

		Button btAddAttractor3 = controlP5
				.addButton("Init", 0, 340, 10, 50, 20);
		// btAddAttractor.setLabel("add attractor");
		btAddAttractor3.addListener(new ControlListener() {

			@Override
			public void controlEvent(ControlEvent e) {
				// initPhysics();
				bang = true;
			}
		});
		btAddAttractor3.setWindow(controlWindow);

		int space = 30;
		// myFrames = controlP5.addTextlabel("frameRate", frameRate+"", 10, 50);
		// myFrames.setWindow(controlWindow);
		Controller mySlider = controlP5.addSlider("mouseRadius", 0, 200f, 10,
				40 + space, 300, 10);
		mySlider.setWindow(controlWindow);
		Controller mySlider2 = controlP5.addSlider("mouseStrength", 0, 10f, 10,
				60 + space, 300, 10);
		mySlider2.setWindow(controlWindow);
		Controller mySlider3 = controlP5.addSlider("SPACE", 0, 20, 10,
				80 + space, 300, 10);
		mySlider3.setWindow(controlWindow);
		Controller mySlider5 = controlP5.addSlider("SPRING_REST", 0, 20, 10,
				100 + space, 300, 10);
		mySlider5.setWindow(controlWindow);
		Controller mySlider4 = controlP5.addSlider("SPRING_STRENGTH", 0, 0.3f,
				10, 120 + space, 300, 10);
		mySlider4.setWindow(controlWindow);
		Controller mySlider6 = controlP5.addSlider("TRANSPARENT", 0, 255, 10,
				140 + space, 300, 10);
		mySlider6.setWindow(controlWindow);
		Controller mySlider8 = controlP5.addSlider("WORLD_DRAG", 0, 1f, 10,
				160 + space, 300, 10);
		mySlider8.setWindow(controlWindow);

		Toggle tog = controlP5.addToggle("ATTRACTION_MODE", false, 10,
				180 + space, 20, 20);
		tog.setWindow(controlWindow);

		tog = controlP5.addToggle("POINT_ANI", false, 100, 180 + space, 20, 20);
		tog.setWindow(controlWindow);

		space += 30;

		Controller mySlider7 = controlP5.addSlider("FILL_LINES", 0, 255, 10,
				200 + space, 300, 10);
		mySlider7.setWindow(controlWindow);
		mySlider7 = controlP5.addSlider("FILL_POINTS", 0, 255, 10, 220 + space,
				300, 10);
		mySlider7.setWindow(controlWindow);
		mySlider7 = controlP5.addSlider("LINE_WEIGHT", 0, 4f, 10, 240 + space,
				300, 10);
		mySlider7.setWindow(controlWindow);

		ColorPicker colorPicker1 = controlP5.addColorPicker("picker", 10,
				160 + space, 255, 20);

		// mySlider.setValue(ISO);

		controlWindow.setTitle("Control");

		controlP5.loadProperties(("mesh2d0.ser"));

	}

	private void initPhysics() {
		timeStamp = frameCount;
		// TODO Auto-generated method stub

		physics = new VerletPhysics2D();
		physics.setWorldBounds(new Rect(0, 0, width, height));
		physics.setDrag(WORLD_DRAG);

		GravityBehavior2D grav = new GravityBehavior2D(new Vec2D(0, 0));

		attractors = new ArrayList<AttractionBehavior2D>();

		/*
		 * if(physics != null){ physics.clear(); physics.springs.clear();
		 * physics.particles.clear(); }
		 */

		DIMX = (width / SPACE) + 1;
		DIMY = (height / SPACE) + 1;

		for (int y = 0, idx = 0; y < DIMY; y++) {
			for (int x = 0; x < DIMX; x++) {
				VerletParticle2D p = new VerletParticle2D(x * SPACE, y * SPACE);
				physics.addParticle(p);
				if (x == 0 || x == DIMX - 1 || y == 0 || y == DIMY - 1) {
					p.lock();
					// println(p.toString());
				}
				if (x > 0) {
					VerletSpring2D s = new VerletSpring2D(p,
							physics.particles.get(idx - 1), SPRING_REST,
							SPRING_STRENGTH);
					physics.addSpring(s);
				}
				if (y > 0) {
					VerletSpring2D s = new VerletSpring2D(p,
							physics.particles.get(idx - DIMX), SPRING_REST,
							SPRING_STRENGTH);
					physics.addSpring(s);
				}
				idx++;
			}
		}

		/*
		 * VerletParticle2D p=physics.particles.get(0); VerletParticle2D
		 * q=physics.particles.get(physics.particles.size()-1); float
		 * len=sqrt(sq(REST_LENGTH*(DIM-1))*2); VerletSpring2D s=new
		 * VerletSpring2D(p,q,len,INNER_STRENGTH); physics.addSpring(s);
		 * 
		 * p=physics.particles.get(DIM-1);
		 * q=physics.particles.get(physics.particles.size()-DIM); s=new
		 * VerletSpring2D(p,q,len,INNER_STRENGTH); physics.addSpring(s);
		 */

		// head=physics.particles.get((DIM-1)/2);
		// head.lock();

	}

	@Override
	public void keyPressed() {
		if (key == 'g' || key == 'G') {

		}

		if (key == 'c') {
			noCursor();
		}
		if (key == 'C') {
			cursor(HAND);
		}

		if (key == ',') {
			controlWindow.hide();
		}
		if (key == '.') {
			controlWindow.show();
		}
	}

	/**
	 * On mouse drag add particles at mouse location, if the number of particles
	 * created is less than NUM_PARTICLES
	 */
	@Override
	public void mouseDragged() {
		mousePos.set(mouseX, mouseY);

	}

	@Override
	public void mousePressed() {
		mousePos = new Vec2D(mouseX, mouseY);

		mouseAttractor = new AttractionBehavior2D(mousePos, mouseRadius,
				(ATTRACTION_MODE == true ? -1 : 1) * mouseStrength);
		physics.addBehavior(mouseAttractor);
	}

	/**
	 * On mouse release remove mouse attarction behavior
	 */
	@Override
	public void mouseReleased() {
		physics.removeBehavior(mouseAttractor);
	}

	// called after each message bundle
	// representing the end of an image frame
	@Override
	public void refresh(TuioTime bundleTime) {
	}

	// called when a cursor is removed from the scene
	@Override
	public void removeTuioCursor(TuioCursor tcur) {
		// println("remove cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+")");
		// physics.removeBehavior(mouseAttractor);
		physics.removeBehavior(attractorMap.get(tcur.getCursorID()));
	}

	// called when an object is removed from the scene
	@Override
	public void removeTuioObject(TuioObject tobj) {
		// println("remove object "+tobj.getSymbolID()+" ("+tobj.getSessionID()+")");
	}

	@Override
	public void setup() {

		size(600, 600, OPENGL);

		// hint( DISABLE_OPENGL_2X_SMOOTH );

		// gl = ((PGraphicsOpenGL)g).gl;

		frameRate(400);

		tuioClient = new TuioClient();
		tuioClient.addTuioListener(this);
		tuioClient.connect();

		initP5();
		initPhysics();

	}

	public void setWindowBorder() {
		if (frame != null && presentMode) {
			frame.removeNotify();
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.setLocation(0, 0);
			frame.addNotify();
		}
	}

	// called when a cursor is moved
	@Override
	public void updateTuioCursor(TuioCursor tcur) {

		attractorMap.get(tcur.getCursorID()).getAttractor()
				.set(tcur.getScreenX(width), tcur.getScreenY(height));
	}

	// called when an object is moved
	@Override
	public void updateTuioObject(TuioObject tobj) {
		// println("update object "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
		// +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel());
	}
}
