cd C:\Users\USER\eclipse-workspace\Dwarf
git init
git rm EZgit.bat --cached

git add --all
git commit -m "Added scanline triangle rasterization."
git branch -M Dev 
git remote add origin https://github.com/Hyrdaboo/DwarfGameEngine/tree/Dev 
git push -u origin Dev
exit