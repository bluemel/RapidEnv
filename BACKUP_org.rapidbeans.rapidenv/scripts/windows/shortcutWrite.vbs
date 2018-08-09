'##############################################################################################
'# RapidEnv: shortcutCreate.vbs
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
'# Create or modify a Windows shortcut
'#######################################################
WScript.Echo "Creating or modifying shortcut """ & WScript.Arguments(0) & """"
Set wsShell = CreateObject("WScript.Shell")
Set shortcut = wsShell.CreateShortcut(WScript.Arguments(0))
shortcut.Description = Replace(Replace(WScript.Arguments(1), "&quot;", """"), "\\", "\")
WScript.Echo "shortcut.Description = """ & shortcut.Description & """"
shortcut.TargetPath = WScript.Arguments(2)
WScript.Echo "shortcut.TargetPath = """ & shortcut.TargetPath & """"
shortcut.Arguments = Replace(Replace(WScript.Arguments(3), "&quot;", """"), "\\", "\")
WScript.Echo "shortcut.Arguments = " & shortcut.Arguments
shortcut.WorkingDirectory = WScript.Arguments(4)
WScript.Echo "shortcut.WorkingDirectory = """ & shortcut.WorkingDirectory & """"
shortcut.IconLocation = WScript.Arguments(5)
WScript.Echo "shortcut.IconLocation = """ & shortcut.IconLocation & """"
shortcut.WindowStyle = WScript.Arguments(6)
WScript.Echo "shortcut.WindowStyle = """ & shortcut.WindowStyle & """"
shortcut.HotKey = WScript.Arguments(7)
WScript.Echo "shortcut.HotKey = """ & shortcut.HotKey & """"
shortcut.Save
Set shortcut = Nothing
Set wsShell = Nothing
