const fs = require('fs')
const path = require('path')


const mkdirSync = function (dirPath) {
  try {
    fs.mkdirSync(dirPath)
  } catch (err) {
    if (err.code !== 'EEXIST') throw err
  }
}


function copyFileSync( source, target ) {

    var targetFile = target;

    //if target is a directory a new file with the same name will be created
    if ( fs.existsSync( target ) ) {
        if ( fs.lstatSync( target ).isDirectory() ) {
            targetFile = path.join( target, path.basename( source ) );
        }
    }

    fs.writeFileSync(targetFile, fs.readFileSync(source));
}

function copyFolderRecursiveSync( source ) {
    var files = [];
	console.log("Installing react-native-ble-quick-sdk-bds-plugin to Bluetooth Developer Studio");

		
	var bdsRootPath = process.env.npm_config_bdsPath;
	if ( !fs.existsSync( bdsRootPath ) ) {
		console.log("");
		console.log("!!!!!!!!!!!!!!!!!! ERROR !!! ERROR !!! ERROR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		console.log("");
		console.error("Could not find Bluetooth Developer Studio. Either it is not intalled on your PC or you have provided wrong path" );
		console.error("Please specify Bluetooth Developer Studio's correct root path using argument --bdsPath and then do npm install again" );
		console.error("For example - npm install https://github.com/eric2036/react-native-ble-quick-sdk --bdsPath=\"C:\\Program Files\\something \"" );
		console.log("");
		console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		console.log("");
		return;
	}
	
	bdsPluginPath =  bdsRootPath + "\\\\Plugins\\\\";
	///console.log("Eric -  Bluetooth Developer Studio " + bdsPluginPath);
    //check if folder needs to be created or integrated
    var targetFolder = path.join( bdsPluginPath, path.basename( source ) );
	//console.log("Eric -  targetFolder " + targetFolder);


    if ( !fs.existsSync( targetFolder ) ) {
        fs.mkdirSync( targetFolder );
    }


    //copy
    if ( fs.lstatSync( source ).isDirectory() ) {
        files = fs.readdirSync( source );
        files.forEach( function ( file ) {
            var curSource = path.join( source, file );
            if ( fs.lstatSync( curSource ).isDirectory() ) {
                copyFolderRecursiveSync( curSource, targetFolder );
            } else {
                copyFileSync( curSource, targetFolder );
            }
        } );
    }
	
	console.log("");
	console.log("****************************************************************************");	
	console.log("react-native-ble-quick-sdk-bds-plugin has been installed to Bluetooth Development Studio");
	console.log("****************************************************************************");	
	console.log("");
}

copyFolderRecursiveSync('./bds_plugin/react-native-ble-quick-sdk-bds-plugin');

