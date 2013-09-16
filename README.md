[![Build Status](https://monkeybymonkey.ci.cloudbees.com/buildStatus/icon?job=texturepacker-maven-plugin)](https://monkeybymonkey.ci.cloudbees.com/job/texturepacker-maven-plugin/)

# texturepacker-maven-plugin

This Maven plugin integrates [LibGDX](http://libgdx.badlogicgames.com)'s TexturePacker within your project. It defines a `pack` goal, which executes during the `process-resources` phase by default.

## Configuration

The plugin exposes every configuration option of TexturePacker. The `inputDir`, `outputDir` and `packFileName` parameters are required. Full parameter reference can be found [here](http://monkeybymonkey.org/texturepacker-maven-plugin/pack-mojo.html).

## Example usage

Assuming a standard LibGDX archetype multi-module layout:

    .
    ├── assets
    ├── core
    ├── desktop
    ├── ouya
    └── pom.xml

to your `assets` folder add two new folders, `managed` and `unmanaged`. This is not something mandated by the plugin, just a suggestion. 

Next, add a pom.xml to your assets folder (and declare it a module in your parent POM):

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>pm.monkey.mygame</groupId>
    <artifactId>mygame</artifactId>
    <version>0.1.0-SNAPSHOT</version>
  </parent>
 
  <artifactId>mygame-assets</artifactId>
  <name>My Game (assets)</name>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>2.6</version>
      </plugin>

      <plugin>
        <groupId>pm.monkey.maven.plugins.texturepacker</groupId>
        <artifactId>texturepacker-maven-plugin</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <configuration>
          <inputDir>managed/gfx</inputDir>
          <outputDir>${project.build.outputDirectory}/gfx</outputDir>
          <packFileName>pack.atlas</packFileName>
        </configuration>
        <executions>
          <execution>
            <id>pack</id>
            <goals>
              <goal>pack</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>

    <resources>
      <resource>
        <directory>unmanaged</directory>
      </resource>
    </resources>
  </build>
</project>
```

With this setup, `unmanaged` assets are copied directly to the output, while `managed` assets are processed by the plugin.

Don't forget to declare dependency on the assets module from your various platform modules and adjust their asset paths to match `outputDir`.

## IDE support

The plugin is compatible with [m2eclipse](http://eclipse.org/m2e/). To repack your textures, clean the 'assets' project to trigger a rebuild.

## License

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.