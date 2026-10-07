# -*- coding: utf-8 -*-
import io, sys

p = r'I:\mods\OptiFabric\dist\README.txt'
with io.open(p, 'r', encoding='utf-8', newline='') as f:
    text = f.read()

report = []

def replace_once(text, old, new, label):
    n = text.count(old)
    report.append('hits=%d  %s' % (n, label))
    if n != 1:
        return text
    return text.replace(old, new)

# 1. sizes on the two current-version lines (numbers only, ASCII-safe anchors)
text = replace_once(text,
    'OptiFabric-Reforged-2.2.3+mc26.2.jar        562 \u4e2a\u8865\u4e01\u7c7b   180471 \u5b57\u8282',
    'OptiFabric-Reforged-2.2.3+mc26.2.jar        562 \u4e2a\u8865\u4e01\u7c7b   188876 \u5b57\u8282',
    'mc26.2 size')
text = replace_once(text,
    'OptiFabric-Reforged-2.2.3+mc26.1.2.jar      567 \u4e2a\u8865\u4e01\u7c7b   180474 \u5b57\u8282',
    'OptiFabric-Reforged-2.2.3+mc26.1.2.jar      567 \u4e2a\u8865\u4e01\u7c7b   188879 \u5b57\u8282',
    'mc26.1.2 size')

# 2. a 2.2.3 note in front of the existing 2.2.2 note
old_note = u'    (2.2.2 \u53ea\u6539\u58f0\u660e\u4e0e\u6587\u6863'
new_note = (u'    (2.2.3 \u4fee\u7684\u662f\u201c\u8d77\u4e0d\u6765\u201d\uff1a\u7ed9 OptiFine \u90a3\u4efd '
            u'GuiRenderer$Draw \u8865\u4e0a Fabric API \u7684 accessor \u63a5\u53e3\uff1b\r\n'
            u'     \u63a5\u53e3\u6309\u7c7b\u8def\u5f84\u67e5\u627e\u3001\u4e0d\u5199\u6b7b\u540d\u5b57 \u2014\u2014 '
            u'26.2 \u662f GuiRendererDrawAccessor\u300126.1.2 \u662f DrawAccessor)\r\n'
            u'    (2.2.2 \u53ea\u6539\u58f0\u660e\u4e0e\u6587\u6863')
text = replace_once(text, old_note, new_note, '2.2.3 note')

with io.open(p, 'w', encoding='utf-8', newline='') as f:
    f.write(text)

print('\n'.join(report))
