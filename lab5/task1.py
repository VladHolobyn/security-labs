import string

def encode(text, key, alphabet):
    res = []
    for i in text:
        char_i = alphabet.index(i)
        res.append(alphabet[(char_i+key)% len(alphabet)])
    
    return "".join(res)


def decode(text, key, alphabet):
    res = []
    for i in text:
        char_i = alphabet.index(i)
        new_char_i = char_i-key if char_i-key > -1 else len(alphabet) + (char_i-key)
        res.append(alphabet[new_char_i])
    
    return "".join(res)


def find_key(encoded, alphabet):
    for i in range(len(alphabet)):
        print("key =", i, " value:", decode(encoded, i, alphabet))


C = "vppanlwxlyopyncjae"
find_key(C, string.ascii_lowercase)






ukrainian_alphabet = [
    'а', 'б', 'в', 'г', 'ґ', 'д', 'е', 'є', 'ж', 'з', 'и', 'і', 'ї', 'й', 
    'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 
    'ш', 'щ', 'ь', 'ю', 'я'
]

text = "криптографія"
key = 50

encoded = encode(text, key, ukrainian_alphabet)
decoded = decode(encoded, key, ukrainian_alphabet)

print("Text: ", text)
print("Encoded: ", encoded)
print("Result: ", text == decoded)