
def encode(text, key, alphabet):
    key = key * (len(text) // len(key)+1)

    res = []
    for i in range(len(text)):
        Mi = alphabet.index(text[i])
        Ki = alphabet.index(key[i])
        Ci = alphabet[(Mi+Ki) % len(alphabet)]
        res.append(Ci)

    return "".join(res)

def decode(encoded, key, alphabet):
    key = key * (len(text) // len(key)+1)
    
    res = []
    for i in range(len(encoded)):
        Ci = alphabet.index(encoded[i])
        Ki = alphabet.index(key[i])
        Mi = alphabet[(Ci-Ki + len(alphabet)) % len(alphabet)] 
        res.append(Mi)

    return "".join(res) 


ukrainian_alphabet = [
    'а', 'б', 'в', 'г', 'ґ', 'д', 'е', 'є', 'ж', 'з', 'и', 'і', 'ї', 'й', 
    'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 
    'ш', 'щ', 'ь', 'ю', 'я'
]

text = "криптографічніметодизахистуінформації"
key =  "голобин" 

encoded = encode(text, key, ukrainian_alphabet)
decoded = decode(encoded, key, ukrainian_alphabet)

print("Text: ", text)
print("Encoded: ", encoded)
print("Result: ", text == decoded)

