<?php
$dbHost = "dbHost";
$dbUser = "root";
$dbPass = "toor";
$dbDatabase = "dbName";
//connect to the database
$db = mysql_connect("$dbHost", "$dbUser", "$dbPass") or die ("Error connecting to database.");
mysql_select_db("$dbDatabase", $db) or die ("Couldn't select the database.");
?>
