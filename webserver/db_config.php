<?php
 	$config = json_decode(file_get_contents('/var/www/mysql_config.json'), true);
	define('DB_SERVER', $config['host']);
	define('DB_USER', $config['login']);
	define('DB_PASSWORD', $config['passwd']);
	define('DB_DATABASE', $config['database_footprints']);
	define('DB_TABLE_POSITIONS', "positions");
?>
