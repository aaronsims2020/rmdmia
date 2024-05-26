echo "md5:topic:/turtle1/cmd_vel" >> rosmd5.properties
rosmsg md5 geometry_msgs/Twist >> rosmd5.properties
echo "md5:topic:/turtle1/color_sensor" >> rosmd5.properties
rosmsg md5 turtlesim/Color >> rosmd5.properties
echo "md5:topic:/rosout" >> rosmd5.properties
rosmsg md5 rosgraph_msgs/Log >> rosmd5.properties
echo "md5:topic:/rosout_agg" >> rosmd5.properties
rosmsg md5 rosgraph_msgs/Log >> rosmd5.properties
echo "md5:topic:/turtle1/pose" >> rosmd5.properties
rosmsg md5 turtlesim/Pose >> rosmd5.properties
echo "md5:service:/turtle1/teleport_absolute" >> rosmd5.properties
rossrv md5 turtlesim/TeleportAbsolute >> rosmd5.properties
echo "md5:service:/turtlesim/get_loggers" >> rosmd5.properties
rossrv md5 roscpp/GetLoggers >> rosmd5.properties
echo "md5:service:/turtlesim/set_logger_level" >> rosmd5.properties
rossrv md5 roscpp/SetLoggerLevel >> rosmd5.properties
echo "md5:service:/reset" >> rosmd5.properties
rossrv md5 std_srvs/Empty >> rosmd5.properties
echo "md5:service:/rosout/get_loggers" >> rosmd5.properties
rossrv md5 roscpp/GetLoggers >> rosmd5.properties
echo "md5:service:/spawn" >> rosmd5.properties
rossrv md5 turtlesim/Spawn >> rosmd5.properties
echo "md5:service:/clear" >> rosmd5.properties
rossrv md5 std_srvs/Empty >> rosmd5.properties
echo "md5:service:/rosout/set_logger_level" >> rosmd5.properties
rossrv md5 roscpp/SetLoggerLevel >> rosmd5.properties
echo "md5:service:/turtle1/set_pen" >> rosmd5.properties
rossrv md5 turtlesim/SetPen >> rosmd5.properties
echo "md5:service:/turtle1/teleport_relative" >> rosmd5.properties
rossrv md5 turtlesim/TeleportRelative >> rosmd5.properties
echo "md5:service:/kill" >> rosmd5.properties
rossrv md5 turtlesim/Kill >> rosmd5.properties
