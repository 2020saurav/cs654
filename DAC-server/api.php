<?php

include ("db_cred.php");

$input    = utf8_decode(urldecode($_POST["input"]));
$response = "";

if (checkSanity($input)) {
    // check in database
    $time = time();
    $queryResponse = mysqli_query($db, "SELECT result FROM dac WHERE expression='$input'");
    if ((mysqli_num_rows($queryResponse)) == 0) {
        try {
            eval( '$result = (' . $input . ');' );
            if (is_numeric ($result)) {
                $response = $result;
            } else {
                $response = "Bad Input";
                error_log("Not numeric Input");
            }
        } catch (Exception $e) {
            $response = "Bad Input";
            error_log("Exception caught in eval", $e);
        }
        echo ($response);
        mysqli_query($db, "INSERT INTO dac VALUES ('$input', '$response', '$time')");

    } else {
        $result = mysqli_fetch_assoc($queryResponse);
        echo (floatval($result['result']));
        mysqli_query($db, "UPDATE dac SET last_used = '$time' WHERE expression = '$input'");
    }
} else {
    $response = "Bad Input";
    echo ($response);
    error_log("Input not sane");
}

// Checks if expression has only numbers, decimal or operators
function checkSanity($string) {
    return preg_match('/^[0-9|.|+|\-|*|\/]*$/', $string);
}
