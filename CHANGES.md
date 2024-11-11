# Changelog
### Version 1.0.9
* The default angle no longer resets after deactivating and reactivating the angle property. 
### Version 1.0.8
* Fixed crash when changing the preview background color
* Fixed crash on Mac when opening file picker dialogs
### Version 1.0.7
* Adding images now inserts the new images after the currently selected image
* Added user configurable FPS setting
* Added ability to reload the emitter images. Press F5 or click the button in the images panel.
* The preview is no longer dimmed when the color picker for the color graphs are drawn
* The preview interface is now visible over the particle effect and preview background
* Minor UI tweaks and bug fixes
### Version 1.0.6
* Resolved issue with Angle still affecting the emitter despite being deactivated. Thanks ludek17052.
* The position of a newly loaded effect is now set to the position of the old one. Thanks ChinQingl.
### Version 1.0.5
* Fix export file dialog defaulting to an incorrect path
* Fixed image name error when loading old particle effects with whole paths listed in the particle file
* Fixed crash when pressing the Ctrl+Tab shortcut when the screen is defaulted to anything other than "Welcome"
* Fixed crash when setting a negative max count. Thanks Yannoch!
* Minor bugfixes and code cleanup
### Version 1.0.4
* Added "Export" option to allow users to save a copy of their particle file
* Fixed tooltips for "zoom in" and "zoom out"
* Fixed tooltip for "Aligned" option
* Fixed being unable to right-click the preview panel over the statistics label
* Fixed unable to delete the correct sprite if particles have the same file name
* Minor bugfixes and code cleanup
### Version 1.0.3
* Added date and time to log errors
* Fixed incorrect image paths when loading from a CRLF file. Thanks Obigu!
* Fixed loading old particle effect files. Thanks Obigu!
* Fixed error messages not being able to be dismissed or interacted with after an image path error
### Version 1.0.2
* Added option to disable exporting images with the particle file
* Locate Images no longer copies files to the particle effect's parent folder
* Locate Images only requires you to find the first missing image if the rest of the images are in the same folder
* Fixed reading files with CRLF end of line sequences. Thanks Obigu!
* Fixed stage focus when the locate images dialog is shown
* Fixed issues with mice that send two touch-downs instead of one. Thanks Nate!
* Fixed incorrect order of emitters in the preview when merging effects
* Fixed unable to add new nodes to the ColorGraph
* Minor bugfixes and code cleanup
### Version 1.0.1
* Removed all references to Particle Park Pro
* Escape hides relevant dialogs
* Fixed crash when an open dialog returns a file that does not exist
* Fixed unable to set keyboard shortcuts
* Minor bugfixes and code cleanup
### Version 1.0.0
* Rebranded app as GDX Particle Editor
* Fixed PPM not being applied at app start
### Version 0.0.4
* Keyboard shortcuts to save, saveAs, and open have been added. Thanks, John!
* Added option to load pre-built templates
* Added drag and drop functionality for images and particle files
* Prompt the user to save changes before exiting the app, loading a particle effect file, or loading a template
* The color of the statistics label can be changed
* The preview settings are persisted and can be reset to default values
* Right-clicking the preview now hides the cursor
* Opening a particle effect file that doesn't have the locally saved particle images asks you to locate them in a new dialog
* The currently open file is displayed in the window title and indicates if changes have been made
* The particle effect now resets when certain properties are modified so the change is instantly noticeable
* The split pane and carousel in Wizard mode are now persisted between switching modes
* Fixed crash upon deactivating all emitters. Thanks, Rafa!
* Fixed merging particles resulting in the wrong order of particles being added to the particle effect
* Fixed independent checkbox for emitters not working
* Fixed merging resulting in unsorted and unexpected results
* Fixed undoing remove image always adding the image to the end of the list
* Fixed EditableLabel not changing back to a label on an unfocus
* Fixed Size subpanel not showing the splitx/y controls when appropriate
* Minor bugfixes and code cleanup
### Version 0.0.3
* Fixed certain keyboard shortcuts being triggered at the same time
* Fixed disabled emitters not being exported when saving
* Fixed tooltips staying visible when the mouse exits the window
* Fixed file extension not being saved on Mac
* Window size is initialized to a percentage of the available screen size
* Added more details to the summary of the Wizard mode
* Added the Delta Multiplier functionality of the preview
* Added the Pixels Per Meter functionality of the preview
* Decimal units for values allowed where it makes sense
* Sharper fonts when UI is scaled.
* Updated libGDX version to 1.12.1
* Minor bugfixes and code cleanup
### Version 0.0.2
* Initial release version
### Version 0.0.1
* Created for alpha testing and proof of concept
