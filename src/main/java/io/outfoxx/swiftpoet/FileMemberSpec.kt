/*
 * Copyright 2018 Outfox, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.outfoxx.swiftpoet

class FileMemberSpec internal constructor(builder: Builder) {
  val doc = builder.doc.build()
  val member = builder.member
  val guardTest = builder.guardTest.build()

  internal fun emit(out: CodeWriter): CodeWriter {

    out.emitDoc(doc)

    if (guardTest.isNotEmpty()) {
      out.emit("#if ")
      out.emitCode(guardTest)
      out.emit("\n")
    }

    when (member) {
      is TypeSpec -> member.emit(out)
      is FunctionSpec -> member.emit(out, null, setOf(Modifier.PUBLIC))
      is PropertySpec -> member.emit(out, setOf(Modifier.PUBLIC))
      is TypeAliasSpec -> member.emit(out)
      is ExtensionSpec -> member.emit(out)
      else -> throw AssertionError()
    }

    if (guardTest.isNotEmpty()) {
      out.emit("#endif")
      out.emit("\n")
    }

    return out
  }

  class Builder internal constructor(internal val member: Any) {
    internal val doc = CodeBlock.builder()
    internal val guardTest = CodeBlock.builder()

    fun addDoc(format: String, vararg args: Any) = apply {
      doc.add(format, *args)
    }

    fun addDoc(block: CodeBlock) = apply {
      doc.add(block)
    }

    fun addGuard(test: CodeBlock) = apply {
      guardTest.add(test)
    }

    fun addGuard(format: String, vararg args: Any) = apply {
      addGuard(CodeBlock.of(format, args))
    }

    fun build(): FileMemberSpec {
      return FileMemberSpec(this)
    }
  }

  companion object {
    @JvmStatic fun builder(member: AnyTypeSpec) = Builder(member)

    @JvmStatic fun builder(member: FunctionSpec) = Builder(member)

    @JvmStatic fun builder(member: PropertySpec) = Builder(member)

    @JvmStatic fun builder(member: ExtensionSpec) = Builder(member)
  }

}