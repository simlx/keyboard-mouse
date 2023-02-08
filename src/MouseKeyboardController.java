import static java.lang.System.out;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

class StatusWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	public JLabel label;

	public StatusWindow() {
		setSize(250, 250);
		label = new JLabel();
		label.setText("test");
		add(label);

		setVisible(true);
	}
}

public class MouseKeyboardController implements NativeKeyListener, ActionListener {

	private static final int KEY_DOWN = 57424;
	private static final int KEY_RIGHT = 57421;
	private static final int KEY_UP = 57416;
	private static final int KEY_LEFT = 57419;
	private static final int KEY_R_SHIFT = 3638;

	StatusWindow w;
	boolean active = false;
	boolean cancelNextKey = false;
	boolean ctrlWasPressed = false;
	boolean shiftPressed = false;
	boolean rightShiftPressed = false;
	boolean control = false;

	boolean left = false;
	boolean right = false;
	boolean up = false;
	boolean down = false;

	boolean mouse1 = false;
	boolean mouse2 = false;

	Timer timer;
	Robot robot;

	public MouseKeyboardController() {
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this);
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			robot = new Robot();
		} catch (Exception e) {
			out.println(e.getMessage());
		}

		w = new StatusWindow();

		timer = new Timer(5, this);
		timer.start();
	}

	public static void main(String args[]) {
		new MouseKeyboardController();
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nke) {

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nke) {
		try {
			if (cancelNextKey) {
				cancelNextKey = false;
				return;
			}

			if (nke.getKeyCode() != 56)
				ctrlWasPressed = false;

			switch (nke.getKeyCode()) {

			case KEY_R_SHIFT:
				rightShiftPressed = true;
				break;

			case 29:
				control = true;

				if (!active || mouse1)
					return;

				if (!rightShiftPressed) {
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					mouse1 = true;
				} else {
					robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					mouse2 = true;
				}

				break;

			case 56:
				if (control) {
					active = !active;
					w.label.setText(active + "");
				}
				break;

			case 42:
				shiftPressed = true;
				break;

			case KEY_LEFT:
				if (!active)
					return;
				left = true;
				if (mouse1)
					return;
				cancelNextKey = true;
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				break;

			case KEY_RIGHT:
				if (!active)
					return;
				right = true;
				if (mouse1)
					return;
				cancelNextKey = true;
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				break;

			case KEY_DOWN:
				if (!active)
					return;
				down = true;
				if (mouse1)
					return;
				cancelNextKey = true;
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_UP);
				break;

			case KEY_UP:
				if (!active)
					return;
				up = true;
				if (mouse1)
					return;
				cancelNextKey = true;
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);
				break;
			}

		} catch (Exception e) {
			out.println(e.getMessage());
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nke) {
		if (nke.getKeyCode() == 29) {
			if (mouse1) {
				mouse1 = false;
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			}
			if (mouse2) {
				mouse2 = false;
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			}
		}

		if (nke.getKeyCode() == 29)
			control = false;
		if (nke.getKeyCode() == KEY_R_SHIFT)
			rightShiftPressed = false;
		if (nke.getKeyCode() == 42)
			shiftPressed = false;
		if (nke.getKeyCode() == 57424)
			down = false;
		if (nke.getKeyCode() == 57421)
			right = false;
		if (nke.getKeyCode() == 57416)
			up = false;
		if (nke.getKeyCode() == 57419)
			left = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (active) {
				int speed = (10 * (shiftPressed ? 4 : 1)) + (10 * (rightShiftPressed ? 5 : 0));
				int x = MouseInfo.getPointerInfo().getLocation().x;
				int y = MouseInfo.getPointerInfo().getLocation().y;
				if (left) {
					robot.mouseMove(x - speed, y);
					x = MouseInfo.getPointerInfo().getLocation().x;
					y = MouseInfo.getPointerInfo().getLocation().y;
				}
				if (right) {
					robot.mouseMove(x + speed, y);
					x = MouseInfo.getPointerInfo().getLocation().x;
					y = MouseInfo.getPointerInfo().getLocation().y;
				}
				if (up) {
					robot.mouseMove(x, y - speed);
					x = MouseInfo.getPointerInfo().getLocation().x;
					y = MouseInfo.getPointerInfo().getLocation().y;
				}
				if (down) {
					robot.mouseMove(x, y + speed);
				}
			}
		} catch (Exception e1) {
			out.println(e1.getMessage());
		}
	}
}
