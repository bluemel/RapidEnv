'##############################################################################################
'# RapidEnv: shortcutRead.vbs
'#
'# Copyright (C) 2011 Martin Bluemel
'#
'# Creation Date: 08/14/2011
'#
'# This program is free software; you can redistribute it and/or modify it under the terms of the
'# GNU Lesser General Public License as published by the Free Software Foundation;
'# either version 3 of the License, or (at your option) any later version.
'# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
'# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
'# See the GNU Lesser General Public License for more details.
'# You should have received a copies of the GNU Lesser General Public License and the
'# GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
'##############################################################################################

'#######################################################
'# Read a Windows shortcut
'#######################################################
WScript.Echo "Reading shortcut """ & WScript.Arguments(0) & """" 
Set wsShell = CreateObject("WScript.Shell")
Set shortcut = wsShell.CreateShortcut(WScript.Arguments(0))
WScript.Echo "FullName=" & shortcut.FullName
WScript.Echo "Description=" & shortcut.Description
WScript.Echo "TargetPath=" & shortcut.TargetPath
WScript.Echo "Arguments=" & shortcut.Arguments
WScript.Echo "WorkingDirectory=" & shortcut.WorkingDirectory
WScript.Echo "IconLocation=" & shortcut.IconLocation
WScript.Echo "WindowStyle=" & shortcut.WindowStyle
WScript.Echo "Hotkey=" & shortcut.Hotkey
Set shortcut = Nothing
Set wsShell = Nothing
