package frc.team449.robot2022;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team449.control.auto.AutoRoutine;
import frc.team449.control.holonomic.OIHolonomic;
import frc.team449.control.holonomic.SwerveDrive;
import frc.team449.robot2022.drive.DriveConstants;
import frc.team449.system.AHRS;
import frc.team449.system.encoder.AbsoluteEncoder;
import frc.team449.system.encoder.BackupEncoder;
import frc.team449.system.encoder.NEOEncoder;
import frc.team449.system.encoder.QuadEncoder;
import frc.team449.system.motor.SparkMaxConfig;
import frc.team449.system.motor.WrappedMotor;
import io.github.oblarg.oblog.annotations.Log;
import java.util.function.Supplier;

public final class RobotContainer2022 {

  // Other CAN IDs
  public static final int PDP_CAN = 1, PCM_MODULE = 0;

  public final XboxController driveController = new XboxController(0);

  public final AHRS ahrs = new AHRS(SerialPort.Port.kMXP);

  @Log.Include
  public final SwerveDrive drive;

  public final Supplier<ChassisSpeeds> oi;
  // Instantiate/declare PDP and other stuff here

  public final Field2d field = new Field2d();
  public final SendableChooser<AutoRoutine> autoChooser = new SendableChooser<>();

  public RobotContainer2022() {
    this.drive = createDrivetrain();
    this.oi =
      new OIHolonomic(
        drive,
        driveController::getLeftY,
        driveController::getLeftX,
        () -> driveController.getRawAxis(3),
        new SlewRateLimiter(0.5),
        1.5,
        true
      );
  }

  /** Helper to make turning motors for swerve */
  private static WrappedMotor makeDrivingMotor(
    String name,
    int motorId,
    boolean inverted,
    Encoder wpiEnc
  ) {
    return new SparkMaxConfig()
      .setName(name + "Drive")
      .setId(motorId)
      .setEnableBrakeMode(true)
      .setInverted(inverted)
      .setEncoderCreator(
        BackupEncoder.creator(
          QuadEncoder.creator(
            wpiEnc,
            DriveConstants.DRIVE_EXT_ENC_CPR,
            DriveConstants.DRIVE_UPR,
            1
          ),
          NEOEncoder.creator(
            DriveConstants.DRIVE_UPR,
            DriveConstants.DRIVE_GEARING
          ),
          DriveConstants.DRIVE_ENC_VEL_THRESHOLD
        )
      )
      .build();
  }

  /** Helper to make turning motors for swerve */
  private static WrappedMotor makeTurningMotor(
    String name,
    int motorId,
    boolean inverted,
    int encoderChannel,
    double offset
  ) {
    return new SparkMaxConfig()
      .setName(name + "Turn")
      .setId(motorId)
      .setEnableBrakeMode(true)
      .setInverted(inverted)
      .setEncoderCreator(
        AbsoluteEncoder.creator(
          encoderChannel,
          2 * Math.PI,
          offset,
          DriveConstants.TURN_UPR,
          DriveConstants.TURN_GEARING
        )
      )
      .build();
  }

  private SwerveDrive createDrivetrain() {
    // todo actually make the modules
    return SwerveDrive.squareDrive(
      ahrs,
      DriveConstants.MAX_LINEAR_SPEED,
      DriveConstants.MAX_ROT_SPEED,
      makeDrivingMotor(
        "FL",
        DriveConstants.DRIVE_MOTOR_FL,
        false,
        DriveConstants.DRIVE_ENC_FL
      ),
      makeDrivingMotor(
        "FR",
        DriveConstants.DRIVE_MOTOR_FR,
        false,
        DriveConstants.DRIVE_ENC_FR
      ),
      makeDrivingMotor(
        "BL",
        DriveConstants.DRIVE_MOTOR_BL,
        false,
        DriveConstants.DRIVE_ENC_BL
      ),
      makeDrivingMotor(
        "BR",
        DriveConstants.DRIVE_MOTOR_BR,
        false,
        DriveConstants.DRIVE_ENC_BR
      ),
      makeTurningMotor(
        "FL",
        DriveConstants.TURN_MOTOR_FL,
        false,
        DriveConstants.TURN_ENC_CHAN_FL,
        DriveConstants.TURN_ENC_OFFSET_FL
      ),
      makeTurningMotor(
        "FR",
        DriveConstants.TURN_MOTOR_FR,
        false,
        DriveConstants.TURN_ENC_CHAN_FR,
        DriveConstants.TURN_ENC_OFFSET_FR
      ),
      makeTurningMotor(
        "BL",
        DriveConstants.TURN_MOTOR_BL,
        false,
        DriveConstants.TURN_ENC_CHAN_BL,
        DriveConstants.TURN_ENC_OFFSET_BL
      ),
      makeTurningMotor(
        "BR",
        DriveConstants.TURN_MOTOR_BR,
        false,
        DriveConstants.TURN_ENC_CHAN_BR,
        DriveConstants.TURN_ENC_OFFSET_BR
      ),
      DriveConstants.FRONT_LEFT_LOC,
      () -> new PIDController(0, 0, 0),
      () ->
        new ProfiledPIDController(
          0,
          0,
          0,
          new TrapezoidProfile.Constraints(0, 0)
        ),
      new SimpleMotorFeedforward(0, 0, 0),
      new SimpleMotorFeedforward(0, 0, 0)
    );
  }

  public void teleopInit() {
    // todo Add button bindings here
  }

  public void robotPeriodic() {}

  public void simulationInit() {
    // DriverStationSim.setEnabled(true);
  }

  public void simulationPeriodic() {
    // Update simulated mechanisms on Mechanism2d widget and stuff
  }
}
