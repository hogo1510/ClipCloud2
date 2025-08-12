<!DOCTYPE html>
<html lang="nl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tekst Posten</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }

        .container {
            width: 100%;
            max-width: 600px;
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            padding: 30px;
            box-sizing: border-box;
        }

        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 25px;
            font-weight: 600;
        }

        .input-container {
            position: relative;
            margin-bottom: 20px;
        }

        .input-field {
            width: 100%;
            min-height: 150px;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 16px;
            resize: vertical;
            transition: border-color 0.3s, box-shadow 0.3s;
            font-family: inherit;
        }

        .input-field:focus {
            outline: none;
            border-color: #4a90e2;
            box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.2);
        }

        .input-field::placeholder {
            color: #aaa;
        }

        .post-button {
            background: linear-gradient(135deg, #4a90e2 0%, #3a7bd5 100%);
            color: white;
            border: none;
            padding: 12px 25px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            width: 100%;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .post-button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }

        .post-button:active {
            transform: translateY(0);
        }

        .post-button:focus {
            outline: none;
            box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.4);
        }

        .counter {
            text-align: right;
            color: #888;
            font-size: 14px;
            margin-top: 5px;
        }

        .post-feedback {
            text-align: center;
            margin-top: 20px;
            padding: 10px;
            border-radius: 5px;
            opacity: 0;
            transition: opacity 0.3s;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            opacity: 1;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            opacity: 1;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Deel je gedachten</h1>

    <?php
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $message = $_POST['message'] ?? '';

        if (!empty($message)) {
            $url = 'http://localhost:8000/post';
            $data = ['message' => $message];

            $options = [
                'http' => [
                    'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
                    'method'  => 'POST',
                    'content' => http_build_query($data),
                ],
            ];

            $context = stream_context_create($options);
            $result = @file_get_contents($url, false, $context);

            if ($result !== false) {
                echo '<div class="post-feedback success">Bericht succesvol naar Java-server gestuurd: '.htmlspecialchars($result).'</div>';
                
            } else {
                echo '<div class="post-feedback error">Fout: Java-server niet bereikbaar</div>';
            }
        } else {
            echo '<div class="post-feedback error">Voer eerst een bericht in</div>';
        }
    }
    ?>

    <form method="POST" action="">
        <div class="input-container">
            <textarea class="input-field" name="message" placeholder="Schrijf hier je bericht..." id="messageInput"><?= isset($_POST['message']) ? htmlspecialchars($_POST['message']) : '' ?></textarea>
            <div class="counter"><span id="charCount">0</span>/500</div>
        </div>

        <button type="submit" class="post-button" id="postButton">Deel bericht</button>
    </form>
</div>

<script>
    const messageInput = document.getElementById('messageInput');
    const charCount = document.getElementById('charCount');

    // Tekenteller
    messageInput.addEventListener('input', function() {
        const count = this.value.length;
        charCount.textContent = count;

        if (count > 500) {
            charCount.style.color = 'red';
        } else {
            charCount.style.color = '#888';
        }
    });

    // Initialiseer teller
    charCount.textContent = messageInput.value.length;
    if (messageInput.value.length > 500) {
        charCount.style.color = 'red';
    }
</script>
</body>
</html>