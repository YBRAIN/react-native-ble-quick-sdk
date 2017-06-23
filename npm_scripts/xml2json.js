var fs = require('fs');
var path = require('path');
var fastXmlParser = require('fast-xml-parser');

var options = {
		attrPrefix : "",
        textNodeName : "#text",
		ignoreNonTextNodeAttr : false,// true
        ignoreTextNodeAttr : false,// true
		ignoreNameSpace : false //true
	};


function writeToFile(fileName, data){
	fs.writeFile(fileName, data, function (err) {
		if (err) throw err;
		console.log('JSON output has been written to ' + fileName);
	});
}


function prepareOutFilename(xmlFilename, bleProfileType, bleProfileNamespace){


	
	if(bleProfileType == 'service')
	{
		return  "svc_" + prepareJsonFilename(xmlFilename, bleProfileNamespace);
	}
	else if(bleProfileType == 'characteristic')
	{
		return "chr_" + prepareJsonFilename(xmlFilename, bleProfileNamespace);
	}
	
	return null;
}


function prepareJsonFilename(xmlFilename, bleProfileNamespace){
	var search1 =  bleProfileNamespace; 
	var search2 = "org.bluetooth"; 
	var search3 = ".service."; 
	var search4 = ".characteristic."; 
	var search5 = "gap."; 
	
	var result = xmlFilename.replace(search1, "");
	result = result.replace(search2, "");
	result = result.replace(search3, "");
	result = result.replace(search4, "");
	result = result.replace(search5, "");
	
	var search5 = /.xml/gi;
	result = result.replace(search5, "");
	console.log('prepareJsonFilename = ' + result);
	return  result + ".json"
}

function convertXML2Json( xmlFileName, outJsonFileName ) {


    try{
        var xmlData = fs.readFileSync(xmlFileName).toString();
		//if(fastXmlParser.validate(xmlData)=== true)
		{
			var output = JSON.stringify(fastXmlParser.parse(xmlData,options),null,4);
	

			if(outJsonFileName){
				writeToFile(outJsonFileName,output);
			}else{
				console.log(output);
			}
		}
    }catch(e){
        console.log("Seems an invalid file." + e);
    }
	
}


function runConvertAllXml2Json(){
	// arguments are :  --bleNamespace --bleProfileType
	
	var bleProfileNamespace = process.env.npm_config_bleNamespace; //"com.ybrain" // 
	var bleProfileType = process.env.npm_config_bleProfileType; // "service"
	console.log("bleProfileNamespace = " + bleProfileNamespace);
	console.log("bleProfileType = " + bleProfileType);
	
	if(bleProfileType == 'service')
	{
		var xmlSourceDir = ".\\device_services\\profile\\svc\\";  
	}
	else if(bleProfileType == 'characteristic')
	{
		var xmlSourceDir = ".\\device_services\\profile\\chr\\";  
	}
	
	var outJsonFileName = "";
	console.log("xmlSourceDir = " + xmlSourceDir);
    if ( fs.lstatSync( xmlSourceDir ).isDirectory() ) {
        files = fs.readdirSync( xmlSourceDir );
        files.forEach( function ( xmlFile ) {
            var xmlFileWithPath = path.join( xmlSourceDir, xmlFile );
			//console.log("xmlFile = " + xmlFile);
			//console.log("xmlFileWithPath = " + xmlFileWithPath);
            if ( fs.lstatSync( xmlFileWithPath ).isFile() ) { 
					outJsonFileName = xmlSourceDir + prepareOutFilename(xmlFile, bleProfileType, bleProfileNamespace);
					convertXML2Json(xmlFileWithPath, outJsonFileName);
            } else {
				//
            }
        } );
    }
	
}


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

function copyFolderRecursiveSync() {
    var files = [];
	console.log("Generating BLE profile for react-native-ble-quick-sdk ");

	var bleProfileNamespace = process.env.npm_config_bleNamespace; //"com.ybrain" // 
	var bleProfileType = process.env.npm_config_bleProfileType; // "service"
	console.log("bleProfileNamespace = " + bleProfileNamespace);
	console.log("bleProfileType = " + bleProfileType);
	
	
	var bdsProjectRootPath = process.env.npm_config_bdsPrjRootPath;
	if ( !fs.existsSync( bdsProjectRootPath ) ) {
		console.log("");
		console.log("!!!!!!!!!!!!!!!!!! ERROR !!! ERROR !!! ERROR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		console.log("");
		console.error("Could not find Bluetooth Developer Studio project (.bds). Please provide correct path" );
		console.log("");
		console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		console.log("");
		return false;
	}


	if(bleProfileType == 'service')
	{
		source =  bdsProjectRootPath + "\\Services";
		var targetPath = ".\\device_services\\profile\\svc\\";  

	}
	else if(bleProfileType == 'characteristic')
	{
		source =  bdsProjectRootPath + "\\Characteristics";
		var targetPath = ".\\device_services\\profile\\chr\\";  
		
	}
	

    //copy
    if ( fs.lstatSync( source ).isDirectory() ) {
        files = fs.readdirSync( source );
        files.forEach( function ( file ) {
            var curSource = path.join( source, file );
            if ( fs.lstatSync( curSource ).isFile() ) {
             //   copyFolderRecursiveSync( curSource, targetFolder );
            //} else {
                copyFileSync( curSource, targetPath );
            }
        } );
    }
	
	console.log("");
	console.log("****************************************************************************");	
	console.log("BLE profile for react-native-ble-quick-sdk has been generated successfully ");
	console.log("****************************************************************************");	
	console.log("");
	
	return true;
}


if(copyFolderRecursiveSync())
{
	runConvertAllXml2Json();
}
else
{
		console.log("!!!!!!!!!!!!!!!!!! Internal !!! Error !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		console.log("");
}



