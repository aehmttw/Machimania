#Check for JRE
echo "Searching for JRE..."
if(java --version -And jar --version){
    echo "FOUND!"
}else{
    echo "No JRE found, read BUILD.md for instructions"
    exit 1
}

#Check for JDK

echo "Searching for JDK..."
if(javac --version){
    echo "FOUND!"
}else
{
    echo "No JDK found, read BUILD.md for instructions"
    exit 1
}

#Check for Depedencies
echo "Searching for Dependencies..."
if(Test-Path "libs")
{
    echo "FOUND!"
}else{
    echo "Dependencies are not present, please read BUILD.md!"
    exit 1
}

#Actual Script

mkdir build
cd build
echo "Extracting Dependencies..."
$jars = find ../libs -name "*.jar"
foreach($jar in $jars){
    jar -xf $jar
}
echo "Compiling Source..."
cp -r ../src/* .
cp -r ../resources/* .
javac ./main/Machimania.java
rm -rf *.java
echo "Packaging Game..."
jar -cfm Machimania.jar ./META-INF/MANIFEST.MF *
mv Machimania.jar ../Machimania.jar
cd ..
echo "Cleaning Up..."
rm -rf build
echo "Done!"