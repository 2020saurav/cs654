<?php

$input    = utf8_decode(urldecode($_POST["input"]));
error_log($input);

// TODO caching using MySQL or file
if (checkSanity($input)) {
    try {
        eval( '$result = (' . $input . ');' );
        if ($result) {
            $response = $result;
        } else {
            $response = "Bad Input";
        }
    } catch (Exception $e) {
        $response = "Bad Input";
    }
    echo json_encode($response);
} else {
    $response = "Bad Input";
    echo json_encode($response);
}

// TODO see if it can be made more secure
function checkSanity($string) {
    return preg_match('/^[0-9|.|+|\-|*|\/]*$/', $string);
}
