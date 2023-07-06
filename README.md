
<h1 align="center">
  <br>
  <img src="https://github.com/Hyrdaboo/DwarfGameEngine/assets/67780454/055df463-be38-4b63-b100-fd1c1b3bdf1a" width=300 height=300>
  <br>
  Dwarf Game Engine
  <br>
</h1>

<h4 align="center">A Java-based minimalistic game engine and software renderer.</h4>

<p align="center">
  <a href="#screenshots">Screenshots</a> •
  <a href="#about-this-project">About</a> •
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#license">License</a>
</p>

## Screenshots
<img src="https://github.com/Hyrdaboo/DwarfGameEngine/assets/67780454/d461c202-e612-4456-a85a-ae3c6b6c6542" alt="lights gif" width=100%>
<img src="https://github.com/Hyrdaboo/DwarfGameEngine/assets/67780454/e1c2ebc6-a2af-4598-8148-427877def291" alt="screenshot1" width=100%>
<img src="https://github.com/Hyrdaboo/DwarfGameEngine/assets/67780454/fcd8c91e-2a83-4ffc-8328-dcc0a2a08def" alt="screenshot2" width=100%>
<img src="https://github.com/Hyrdaboo/DwarfGameEngine/assets/67780454/9b85de72-3748-4511-a05f-bf5870f2467f" alt="screenshot3" width=100%>

## About This Project
DwarfGameEngine started as a learning project to explore 3D graphics while I was studying Java. Inspired by a tutorial video by javidx9 on YouTube, I set out to develop my own game engine. After almost a year of work, I'm pleased to say that the project has reached a state where I consider it somewhat finished. Along the way, I've gained valuable experience and knowledge, motivating me to continue improving and refining the engine.

## Key Features

* Pixel shaders
* Perspective correct attribute interpolation
* Vertex attributes: normal, worldPos, depth, texcoord
* Built-in phong and diffuse lighting
* Simple OBJ loader
* Scene management
* Screen space 2D renderer
* And more...<br>
These are some of the key features offered by DwarfGameEngine.

### Weaknesses
I'll be honest the engine is severely limited in terms of performance. The 3D rendering is painfully slow partly because it's in software but also due to the fact that the engine is single threaded and I am not very good at optimizing things😅. To get reasonable frame rates you'll have to run the engine on low resolutions. However that's not to say I'm gonna leave it like this. This is only the alpha version so I am planning to come back to this and hopefully improve the performance


## How To Use
To get started, follow these steps:

1. Download the latest release of DwarfGameEngine from the repository and unzip the archive. You can find the download link <a href="https://github.com/Hyrdaboo/DwarfGameEngine/releases/">here</a>
2. Inside the extracted folder, locate "DwarfGameEngine.jar".
3. Add "DwarfGameEngine.jar" to the classpath of your project.
4. Add "DwarfGameEngineSrc.jar" as the source file for the jar. This will help your IDE understand the source code and provide helpful suggestions while you're writing your code.
5. With these configurations in place, you are now ready to start coding some 3D or 2D games. Check out the <a href="https://github.com/Hyrdaboo/DwarfGameEngine/wiki">wiki</a> for more


## License

MIT

---

