<?php
// success.php
$receivedMessage = $_GET['message'] ?? '';
$serverResponse = $_GET['response'] ?? '';
?>
<!DOCTYPE html>
<html lang="nl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bericht Ontvangen</title>
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
            text-align: center;
        }
        h1 {
            color: #4a90e2;
            margin-bottom: 20px;
        }
        .message-box {
            background-color: #f8f9fa;
            border-left: 4px solid #4a90e2;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
        }
        .response-box {
            background-color: #e8f4fd;
            border-left: 4px solid #28a745;
            padding: 15px;
            margin: 20px 0;
            font-style: italic;
        }
        .back-button {
            background: #4a90e2;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 20px;
            transition: background 0.3s;
        }
        .back-button:hover {
            background: #3a7bd5;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ Bericht Succesvol Ontvangen</h1>

        <p>Je bericht is verwerkt door onze server:</p>

        <div class="message-box">
            <strong>Jouw bericht:</strong>
            <p><?php echo htmlspecialchars($receivedMessage); ?></p>
        </div>

        <?php if (!empty($serverResponse)): ?>
        <div class="response-box">
            <strong>Server antwoord:</strong>
            <p><?php echo htmlspecialchars($serverResponse); ?></p>
        </div>
        <?php endif; ?>

        <button class="back-button" onclick="window.location.href='index.php'">Nieuw Bericht Sturen</button>
    </div>
</body>
</html>