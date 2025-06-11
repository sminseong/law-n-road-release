import { Node, mergeAttributes } from '@tiptap/core'

export const VariableNode = Node.create({
    name: 'variable',
    group: 'inline',       // inline group
    inline: true,
    atom: true,            // 내부 편집 차단
    selectable: true,      // 클릭 시 전체 선택

    addAttributes() {
        return {
            name: { default: '' },
            description: { default: '' },
        }
    },

    parseHTML() {
        return [{ tag: 'span[data-variable]' }]
    },

    renderHTML({ node, HTMLAttributes }) {
        return [
            'span',
            mergeAttributes(
                { 'data-variable': '', 'data-name': node.attrs.name },
                HTMLAttributes,
            ),
            `#{${node.attrs.name}}`,
        ]
    },

    addCommands() {
        return {
            setVariable:
                attrs =>
                    ({ commands }) => {
                        return commands.insertContent({ type: this.name, attrs })
                    },
        }
    },
})