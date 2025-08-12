<?php
// Ontvang de verstuurde data
$receivedMessage = $_GET['message'] ?? '';
$serverResponse = $_GET['response'] ?? '';

// Haal berichten op van de Java backend
$messagesUrl = 'http://localhost:8000/get';
$options = [
    'http' => [
        'method' => 'GET',
        'header' => 'Accept: application/json'
    ]
];

$context = stream_context_create($options);
$messagesResponse = @file_get_contents($messagesUrl, false, $context);
if ($messagesResponse === false) {
    die("Fout bij ophalen berichten: " . error_get_last()['message']);
}

// Debug output
echo "<!-- Raw response: " . htmlspecialchars($messagesResponse) . " -->";
$messages = json_decode($messagesResponse, true);

if (json_last_error() !== JSON_ERROR_NONE) {
    die("JSON parse error: " . json_last_error_msg());
}

// Debug de ontvangen berichten
echo "<!-- Parsed messages: ";
var_dump($messages);
echo " -->";

// Genereer QR code data
$qrData = urlencode($receivedMessage);
$qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" . $qrData;

// Genereer deelbare link
$shareableLink = (isset($_SERVER['HTTPS']) ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
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
            max-width: 800px;
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        h1 {
            color: #4a90e2;
            text-align: center;
            margin-bottom: 30px;
        }
        .section {
            margin-bottom: 30px;
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
        }
        .qr-section {
            text-align: center;
            margin: 25px 0;
        }
        .qr-code {
            max-width: 150px;
            margin: 0 auto;
        }
        .share-link {
            word-break: break-all;
            color: #4a90e2;
            margin: 10px 0;
        }
        .messages-list {
            max-height: 300px;
            overflow-y: auto;
            border: 1px solid #eee;
            border-radius: 8px;
            padding: 10px;
        }
        .message-item {
            padding: 10px;
            border-bottom: 1px solid #eee;
        }
        .message-item:last-child {
            border-bottom: none;
        }
        .message-meta {
            color: #666;
            font-size: 0.9em;
        }
        .button-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 25px;
        }
        .action-button {
            background: #4a90e2;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            transition: background 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        .action-button:hover {
            background: #3a7bd5;
        }
        .copy-button {
            background: #6c757d;
        }
        .copy-button:hover {
            background: #5a6268;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ Bericht Succesvol Ontvangen</h1>

        <div class="section">
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
        </div>

        <div class="section qr-section">
            <h3>Deel je bericht</h3>
            <img src="<?php echo $qrCodeUrl; ?>" alt="QR Code" class="qr-code">
            <div class="share-link"><?php echo htmlspecialchars($shareableLink); ?></div>
            <button class="action-button copy-button" onclick="navigator.clipboard.writeText('<?php echo addslashes($shareableLink); ?>')">Kopieer link</button>
        </div>

        <?php if (!empty($messages)): ?>
        <div class="section">
            <h3>Recente berichten</h3>
            <div class="messages-list">
                <?php foreach ($messages as $message): ?>
                <div class="message-item">
                    <div class="message-meta">
                        <?php echo date('d-m-Y H:i:s', $message['timestamp'] ?? time()); ?>
                    </div>
                    <p><?php echo htmlspecialchars($message['content'] ?? ''); ?></p>
                </div>
                <?php endforeach; ?>
            </div>
        </div>
        <?php endif; ?>

        <div class="button-group">
            <button class="action-button" onclick="window.location.href='index.php'">Nieuw Bericht</button>
            <button class="action-button" onclick="window.location.reload()">Verversen</button>
        </div>
    </div>

    <script>
        // Voeg hier eventuele JavaScript toe voor betere gebruikersinteractie
        document.querySelector('.copy-button').addEventListener('click', function() {
            const originalText = this.textContent;
            this.textContent = 'Gekopieerd!';
            setTimeout(() => {
                this.textContent = originalText;
            }, 2000);
        });
    </script>
</body>
</html>