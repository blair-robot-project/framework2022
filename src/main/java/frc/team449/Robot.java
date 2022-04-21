package frc.team449;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.team449.robot2022.RobotContainer2022;
import io.github.oblarg.oblog.Logger;

/** The main class of the robot, constructs all the subsystems and initializes default commands. */
public class Robot extends TimedRobot {

  private final RobotContainer2022 robotContainer = new RobotContainer2022();
  private Command autoCommand;

  @Override
  public void robotInit() {
    // Yes this should be a print statement, it's useful to know that robotInit started.
    System.out.println("Started robotInit.");

    if (Robot.isSimulation()) {
      // Don't complain about joysticks if there aren't going to be any
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    Logger.configureLoggingAndConfig(robotContainer, false);
    Shuffleboard.setRecordingFileNameFormat("log-${time}");
    Shuffleboard.startRecording();

    SmartDashboard.putData(robotContainer.field);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    robotContainer.robotPeriodic();

    robotContainer.field.setRobotPose(robotContainer.drive.getPose());
  }

  @Override
  public void autonomousInit() {
    var routine = robotContainer.autoChooser.getSelected();
    if (routine != null) {
      this.autoCommand = routine.command;
      robotContainer.field.getObject(routine.name).setTrajectory(routine.traj);
      CommandScheduler.getInstance().schedule(this.autoCommand);
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    if (autoCommand != null) {
      CommandScheduler.getInstance().cancel(autoCommand);
    }
    robotContainer.teleopInit();
  }

  @Override
  public void teleopPeriodic() {
    robotContainer.drive.set(robotContainer.oi.get());
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    if (autoCommand != null) {
      CommandScheduler.getInstance().cancel(autoCommand);
    }
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {
    robotContainer.simulationInit();
  }

  @Override
  public void simulationPeriodic() {
    robotContainer.simulationPeriodic();
  }
}
