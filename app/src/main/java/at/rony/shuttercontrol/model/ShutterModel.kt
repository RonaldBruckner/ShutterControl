package at.rony.shuttercontrol.model

class ShutterModel(windowName: String, codeUp: String, codeStop: String, codeDown: String) {

    var windowName: String = windowName     //name of the shutter, eg kitchen, living room
    var codeUp: String = codeUp             //base64 string for the up-command
    var codeStop: String = codeStop
    var codeDown: String = codeDown

}