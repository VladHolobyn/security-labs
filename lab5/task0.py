
def encode(text, key):
    cols = [[] for i in range(key) ]

    for i in range(len(text)):
        col = i % key
        cols[col].append(text[i])

    return "".join("".join(cols[i]) for i in range(key-1, -1, -1) )

def decode(text, key):
    part_size = len(text) // key
    cols = []

    remainder = len(text) % part_size
    start = (key-1)*part_size
    end = start + part_size + remainder
    
    for i in range(key, 0, -1):
        cols.append(text[start:end])
        end = start
        start -= part_size

    res = []
    for i in range(len(text)):
        col = i % key
        res.append(cols[col][i // key]) 

    return "".join(res)



text = "криптографія"
key = 2

encoded = encode(text, key)
decoded = decode(encoded, key)

print("Text: ", text)
print("Encoded: ", encoded)
print("Result: ", text == decoded)

