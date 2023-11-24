# Changelog
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
