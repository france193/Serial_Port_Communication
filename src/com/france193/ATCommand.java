package com.france193;

/**
 * Created by FLDeviOS on 29/10/2016.
 */
public interface ATCommand {

    /************/
    /** SIMPLE **/
    /************/

    // +++
    // Dynamically switches between DATA and COMMAND mode without changing the physical CMD/UART select switch
    String at_switch_mode = "+++\r\n";

    // ATZ
    // Performs a system reset
    String at_z = "ATZ\r\n";

    // ATI
    // Displays basic information about the Bluefruit module
    String at_i = "ATI\n";

    // AT+HELP
    // Displays a comma-separated list of all AT parser commands available on the system
    String at_help = "AT+HELP\r\n";

    // AT+FACTORYRESET
    // Clears any user config data from non-volatile memory and performs a factory reset before resetting the Bluefruit
    // module
    String at_factory_reset = "AT+FACTORYRESET\r\n";

    // AT+DFU
    // Forces the module into DFU mode, allowing over the air firmware updates using a dedicated DFU app on iOS or
    // Android
    String at_dfu = "AT+DFU";

    // AT+HWRANDOM
    // Generates a random 32-bit number using the HW random number generator on the nRF51822 (based on white noise).
    String at_hwrandom = "AT+HWGETDIETEMP\r\n";

    // AT+HWGETDIETEMP
    // Gets the temperature in degree celcius of the BLE module's die.  This can be used for debug purposes (higher die
    // temperature generally means higher current consumption), but does not corresponds to ambient temperature and can
    // nto be used as a replacement for a normal temperature sensor
    String at_hwgetdietemp = "AT+HWGETDIETEMP\r\n";

    /*************/
    /** COMPLEX **/
    /*************/

    // AT+NVMWRITE
    // Writes data to the 256 byte user non-volatile memory (NVM) region
    // Parameters:
    //              offset: The numeric offset for the first byte from the starting position in the user NVM
    //              datatype: Which can be one of STRING (1), BYTEARRAY (2) or INTEGER (3)
    //              data: The data to write to NVM memory (the exact payload format will change based on the specified
    // datatype)
    // example: AT+NVMWRITE=16,INTEGER,32768

    // AT+NVMREADRAW

    // AT+NVMREAD
    // Reads data from the 256 byte user non-volatile memory (NVM) region
    // Parameters:
    //              offset: The numeric offset for the first byte from the starting position in the user NVM
    //              size: The number of bytes to read
    //              datatype: The type used for the data being read, which is required to properly parse the data and
    // display it as a response.  The value can be one of STRING (1), BYTEARRAY (2) or INTEGER (3)
    // example: AT+NVMREAD=16, 4, INTEGER

    // AT+BAUDRATE
    // Changes the baud rate used by the HW UART peripheral on the nRF51822
    //Parameters: Baud rate, which must be one of the following values:1200 2400 4800 9600 14400 19200 28800 38400
    // 57600 76800 115200 230400 250000 460800 921600 1000000

    /***********/
    /** OTHER **/
    /***********/

    // AT+UARTFLOW
    // AT+HWMODELED
    // AT+HWCONNLED
    // AT+DBGMEMRD
    // AT+DBGNVMRD
    // AT+DBGSTACKSIZE
    // AT+DBGSTACKDUMP
    // AT+HWGPIOMODE
    // AT+HWGPIO
    // AT+HWI2CSCAN
    // AT+HWADC
    // AT+HWVBAT
    // AT+HWPWM
    // AT+HWPWRDN
    // AT+BLEPOWERLEVEL
    // AT+BLEGETADDRTYPE
    // AT+BLEGETADDR
    // AT+BLEGETPEERADDR
    // AT+BLEGETRSSI
    // AT+BLEBEACON
    // AT+BLEURIBEACON
    // AT+EDDYSTONEURL
    // AT+EDDYSTONESERVICEEN
    // AT+EDDYSTONECONFIGEN
    // AT+EDDYSTONEBROADCAST
    // AT+GAPGETCONN
    // AT+GAPDISCONNECT
    // AT+GAPCONNECTABLE
    // AT+GAPDEVNAME
    // AT+GAPDELBONDS
    // AT+GAPINTERVALS
    // AT+GAPSTARTADV
    // AT+GAPSTOPADV
    // AT+GAPAUTOADV
    // AT+GAPSETADVDATA
    // AT+BLEUARTTX
    // AT+BLEUARTRX
    // AT+BLEUARTFIFO
    // AT+BLEBATTEN
    // AT+BLEBATTVAL
    // AT+BLEHIDEN
    // AT+BLEKEYBOARDEN
    // AT+BLEHIDCONTROLKEY
    // AT+BLEKEYBOARDCODE
    // AT+BLEKEYBOARD
    // AT+BLEHIDMOUSEMOVE
    // AT+BLEHIDMOUSEBUTTON
    // AT+BLEHIDGAMEPAD
    // AT+GATTADDSERVICE
    // AT+GATTADDCHAR
    // AT+GATTCHARRAW
    // AT+GATTCHAR
    // AT+GATTLIST
    // AT+GATTCLEAR
    // AT+BLEMIDIEN
    // AT+BLEMIDITX
    // AT+BLEMIDIRXRAW
    // AT+BLEMIDIRX
    // AT+EVENTENABLE
    // AT+EVENTDISABLE
    // AT+EVENTSTATUS
}
