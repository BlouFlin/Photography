# Photography
Yet another Minecraft camera mod to take photos of your world on maps!

_Photography requires [Fabric Loader](https://fabricmc.net/use/installer/) and [Fabric API](https://www.modrinth.com/mod/fabric-api)_

![Photography banner](https://github.com/BlouFlin/Photography/assets/128321038/434aaa62-f7bd-4ea7-a3db-5b99982aebcd)

### **Photography adds a camera to easily take photos. That's it!**
> _This is my very first mod, so please expect a few problems and bugs, even if I've done my best to avoid them. It's also my first public GitHub repository. Thank you!_
> 
#

### How to use the mod

You need two items to take a photo:
- a Camera
- a Photographic Paper

Here are the crafting recipes:

![Camera and Photographic Paper crafting recipes](https://github.com/BlouFlin/Photography/assets/128321038/262810de-fcd2-45ad-97ef-b8fdcc5d6180)

Then use the camera by right-clicking to open the viewfinder. You can use the right-click to take a photo and the escape key to close the viewfinder. Taking a photo will consume a photographic paper from your inventory and give you a photograph.

![Demonstration of how to use the camera](https://github.com/BlouFlin/Photography/assets/128321038/619eb7ca-39ef-4ea0-809b-ccb8c639abd7)

#

### Why yet another camera mod ?

This mod has been created to meet very specific needs. It adds a camera to easily take photos from the client renderer to enable the use of shaders while maintaining compatibility with vanilla clients. To clarify **the mod is required on both server and client sides.** Vanilla clients will still be able to join, but will not be able to see or use the added features (which will be displayed as vanilla items), and the items and photos will remain in the world after the mod is removed.
#

<details>
<summary>Other recommended camera mods for Fabric</summary>

- [Exposure (made by mortuusars)](https://modrinth.com/mod/exposure) : my favorite camera mod with focus on process and aesthetics. It is required on both server and client sides and adds non-vanilla items that disappear after the mod is uninstalled.
- [Camera Obscura (made by tomalbrc)](https://modrinth.com/mod/camera-obscura) : a very impressive server-side mod that allows you to take photos on maps that remain after the mod has been uninstalled. No client-side mods required! Everything is rendered on the server using simple raytracing.
- [Polaroid Camera (made by HyperPigeon)](https://modrinth.com/mod/polaroidcamera) : a very well-made mod that motivated me to try making my own. It lets you take photos on maps that remain after the mod is uninstalled, but adds a non-vanilla camera item that disappears after the mod is uninstalled. It was almost exactly what I needed, but I only wanted vanilla items because I'm a maniac...

</details>

_Photography uses some code from [Image2Map (made by TheEssem and Patbox)](https://modrinth.com/mod/image2map) to render the photos._\
_Many thanks to its authors!_
